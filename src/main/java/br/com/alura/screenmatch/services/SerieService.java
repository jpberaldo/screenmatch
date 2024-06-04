package br.com.alura.screenmatch.services;

import br.com.alura.screenmatch.dto.EpisodioDTO;
import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SerieService {

    @Autowired
    private SerieRepository repositorio;

    public List<SerieDTO> obterTodasSeries() {
        return converteDados(repositorio.findAll());
    }

    public List<SerieDTO> obterTop5Series() {
        return converteDados(repositorio.findTop5ByOrderByAvaliacaoDesc());
    }

    public List<SerieDTO> obterLancamentos() {
        return converteDados(repositorio.ordernarLancamentosPorData());
    }

    public SerieDTO buscarPorId(Long id) {

        Optional<Serie> serie = repositorio.findById(id);
        if (serie.isPresent()) {

            Serie s = serie.get();
            return new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getGenero(), s.getAtores(),
                    s.getPoster(), s.getSinopse());

        } else {
            return null;
        }
    }

    public List<EpisodioDTO> buscarTodasAsTemporadas(Long id) {

        Optional<Serie> serie = repositorio.findById(id);
        if (serie.isPresent()) {

            Serie s = serie.get();
            return s.getEpisodios().stream().map(episodio -> new EpisodioDTO(episodio.getTemporada(), episodio.getTitulo(), episodio.getNumeroEpisodio()))
                    .collect(Collectors.toList());

        } else {
            return null;
        }

    }

    public List<EpisodioDTO> buscarTemporadaPorNumero(Long id, Long temporada) {
        return repositorio.buscarTemporadaPorNumero(id, temporada).stream().map(episodio -> new EpisodioDTO(episodio.getTemporada(), episodio.getTitulo(), episodio.getNumeroEpisodio()))
                .collect(Collectors.toList());
    }

    private List<SerieDTO> converteDados(List<Serie> series) {
        return series.stream()
                .map(s -> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getGenero(), s.getAtores(),
                        s.getPoster(), s.getSinopse())).collect(Collectors.toList());
    }

}
