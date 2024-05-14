package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporadas;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.services.ConsumoAPI;
import br.com.alura.screenmatch.services.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    private Scanner entrada = new Scanner(System.in);
    private final String ENDERECO_URL = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=b7522d9a";
    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();

    public void exibirMenu() {

        System.out.println("Digite o nome da serie");
        var nomeDaSerie = entrada.nextLine();
        var json = consumo.obterDados(ENDERECO_URL + nomeDaSerie.replace(" ", "+") + API_KEY);
        DadosSerie serie = conversor.obterDados(json, DadosSerie.class);
        System.out.println("Dados da serie \n" + serie);


        List<DadosTemporadas> listaTemporadas = new ArrayList<>();
        for (int i = 1; i < serie.totalTemporadas(); i++) {
            json = consumo.obterDados(ENDERECO_URL + nomeDaSerie.replace(" ", "+") + "&Season=" + i + API_KEY);
            //"https://www.omdbapi.com/?t=the+office&Season=" + i + "&apikey=b7522d9a"
            DadosTemporadas dadosTemporada = conversor.obterDados(json, DadosTemporadas.class);
            listaTemporadas.add(dadosTemporada);

        }

        listaTemporadas.forEach(System.out::println);
        listaTemporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        List<DadosEpisodio> dadosEpisodios1 = listaTemporadas.stream()
                .flatMap(t -> t.episodios().stream()).
                collect(Collectors.toList());

        System.out.println("\n TOP 5 episodios");
        dadosEpisodios1.stream().filter(e -> !e.avaliacao().equalsIgnoreCase("N/A")).sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed()).limit(5).forEach(System.out::println);

        List<Episodio> episodios3 = listaTemporadas.stream().flatMap(t -> t.episodios().stream().map(d -> new Episodio(t.numero(), d))).collect(Collectors.toList());
        episodios3.forEach(System.out::println);

        System.out.println("A partir de que ano voce quer ver os episodios?");
        var ano = entrada.nextInt();
        entrada.nextLine();

        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        episodios3.stream().filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca)).forEach(e -> System.out.println(
                "Temporada:" + e.getTemporada() +
                        " Episodio:" + e.getTitulo() +
                        " Data de lancamento:" + e.getDataLancamento().format(formatador)));
    }
}
