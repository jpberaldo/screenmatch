package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.services.ConsumoAPI;
import br.com.alura.screenmatch.services.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class PrincipalJPA {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=b7522d9a"; //minha real api key
    private List<DadosSerie> dadosSerie = new ArrayList<>();
    private SerieRepository repositorio;
    private List<Serie> series = new ArrayList<>();
    private Optional<Serie> serieBusca;

    public PrincipalJPA(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {

        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries buscadas
                    4 - Buscar por titulo
                    5 - Buscar por Ator
                    6 - Listar top 5 séries
                    7 - Buscar series por categoria
                    8 - Buscar por numero maximo de temporadas e avaliação minima
                    9 - Buscar episodios por trecho   
                    10 - Listar top 5 episodios de uma série
                    11 - Buscar episodios por um ano especifico
                     
                                    
                    0 - Sair                                 
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAtor();
                    break;
                case 6:
                    listarTopCincoSeries();
                    break;
                case 7:
                    buscarSeriePorCategoria();
                    break;
                case 8:
                    buscarTemporadaMaximaEAvaliacaoMinima();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    listarTopEpisodios();
                    break;
                case 11:
                    buscarEpisodioPorAno();
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        //dadosSerie.add(dados);
        Serie serie = new Serie(dados);
        repositorio.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {

        listarSeriesBuscadas();
        System.out.println("Escolha uma serie pelo nome: ");
        String nomeSerie = leitura.nextLine();


        Optional<Serie> first = repositorio.findByTituloContainingIgnoreCase(nomeSerie);
        if (first.isPresent()) {

            var serieEncontrada = first.get();
            List<DadosTemporadas> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporadas dadosTemporada = conversor.obterDados(json, DadosTemporadas.class);
                temporadas.add(dadosTemporada);
            }

            temporadas.forEach(System.out::println);
            List<Episodio> episodios = temporadas.stream().flatMap(d -> d.episodios().stream().map(e -> new Episodio(d.numero(), e))).collect(Collectors.toList());
            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);

        } else {
            System.out.println("Serie nao encontrada");
        }
    }

    private void buscarSeriePorTitulo() {

        System.out.println("Escolha uma serie pelo Titulo: ");
        String nomeSerie = leitura.nextLine();

        serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBusca.isPresent()) {
            System.out.println("Dados da serie pesquisada pelo titulo: " + serieBusca.get());
        } else {
            System.out.println("Serie nao encontrada");
        }
    }

    private void listarSeriesBuscadas() {

        series = repositorio.findAll();
        series.stream().sorted(Comparator.comparing(Serie::getGenero)).forEach(System.out::println);

    }

    private void buscarSeriePorAtor() {

        System.out.println("Escolha um Ator: ");
        String nomeAtor = leitura.nextLine();

        List<Serie> atoresEncontrados = repositorio.findByAtoresContainingIgnoreCase(nomeAtor);

        if (!atoresEncontrados.isEmpty()) {
            atoresEncontrados.forEach(System.out::println);
        } else {
            System.out.println("Ator nao encontrado");
        }
    }

    private void listarTopCincoSeries() {
        List<Serie> seriesTop = repositorio.findTop5ByOrderByAvaliacaoDesc();
        seriesTop.forEach(s -> System.out.println(s.getTitulo() + " | avaliacao: " + s.getAvaliacao()));
    }

    private void buscarSeriePorCategoria() {

        System.out.println("Deseja buscar serie de qual categoria/genero");
        String nomeGenero = leitura.nextLine();
        Categoria categoria = Categoria.fromStringPortugues(nomeGenero);
        List<Serie> listaCategoria = repositorio.findByGenero(categoria);
        System.out.println("Series da categoria: " + nomeGenero);
        listaCategoria.forEach(System.out::println);

    }

    private void buscarTemporadaMaximaEAvaliacaoMinima() {

        System.out.println("Quantas temporadas no máximo vc deseja que tenha a serie?");
        var totalTemporadas = leitura.nextInt();
        leitura.nextLine();

        System.out.println("Qual a media minima de avaliacao, tem que ter a serie?");
        var avaliacao = leitura.nextDouble();
        leitura.nextLine();

        List<Serie> lista = repositorio.filtrarPorTemporadasEAvaliacao(totalTemporadas, avaliacao);
        System.out.println("Series filtradas");
        lista.forEach(s -> System.out.println(s.getTitulo() + "  - avaliação: " + s.getAvaliacao()));
    }

    private void buscarEpisodioPorTrecho() {

        System.out.println("Digite alguma parte de algum episodio");
        var buscarTrechoDeEpisodio = leitura.nextLine();

        List<Episodio> episodiosEncontrados = repositorio.episodiosPorTrecho(buscarTrechoDeEpisodio);
        episodiosEncontrados.forEach(e -> System.out.println("Nome da Serie:" + e.getSerie().getTitulo() + " |Temporada:" + e.getTemporada() + " |Nome do episodio: " + e.getTitulo()));

    }

    private void listarTopEpisodios() {

        buscarSeriePorTitulo();
        if (serieBusca.isPresent()) {

            Serie serie = serieBusca.get();
            List<Episodio> topEpisodios = repositorio.topEpisodiosPorSerie(serie);
            topEpisodios.forEach(e -> System.out.println("Série: " + e.getSerie().getTitulo() +
                    " - Temporada: " + e.getTemporada() + " - Numero Episodio: " + e.getNumeroEpisodio() + " - Avaliação: " + e.getAvaliacao() +
                    " - Episódio: " + e.getTitulo()));
        }

    }

    private void buscarEpisodioPorAno() {

        buscarSeriePorTitulo();
        if (serieBusca.isPresent()) {

            Serie serie = serieBusca.get();
            System.out.println("Digite o ano");
            int anoLancamento = leitura.nextInt();
            leitura.nextLine();

            List<Episodio> episodiosAno = repositorio.episodiosPorSerieEAno(serie, anoLancamento);
            episodiosAno.forEach(e -> System.out.println("Série: " + e.getSerie().getTitulo() + " - Ano de Lançamento: " + e.getDataLancamento() +
                    " - Temporada: " + e.getTemporada() + " - Numero Episodio: " + e.getNumeroEpisodio() + " - Avaliação: " + e.getAvaliacao() +
                    " - Episódio: " + e.getTitulo()));
        }
    }

}
