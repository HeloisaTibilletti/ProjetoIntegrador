package ifpr.pgua.eic.colecaomusicas.repositories;

import java.time.LocalDate;
import java.util.List;

import com.github.hugoperlin.results.Resultado;

import ifpr.pgua.eic.colecaomusicas.daos.ArtistaDAO;
import ifpr.pgua.eic.colecaomusicas.daos.GeneroDAO;
import ifpr.pgua.eic.colecaomusicas.daos.MusicaDAO;
import ifpr.pgua.eic.colecaomusicas.models.Artista;
import ifpr.pgua.eic.colecaomusicas.models.Genero;
import ifpr.pgua.eic.colecaomusicas.models.Musica;

public class RepositorioMusicas {

    private MusicaDAO dao;
    private ArtistaDAO artistaDAO;
    private GeneroDAO generoDAO;

    public RepositorioMusicas(MusicaDAO dao, ArtistaDAO artistaDAO, GeneroDAO generoDAO) {

        this.dao = dao;
        this.artistaDAO = artistaDAO;
        this.generoDAO = generoDAO;
    }

    public Resultado cadastrarMusica(String nome, int duracao, int anoLancamento, Artista artista, Genero genero) {
        if (nome.isBlank() || nome.isEmpty()) {
            return Resultado.erro("Nome inválido!");
        }

        if (duracao < 0) {
            return Resultado.erro("Duração inválida!");
        }

        if (anoLancamento < 0 || anoLancamento > LocalDate.now().getYear()) {
            return Resultado.erro("Ano de Lançamento inválido!");
        }

        Musica musica = new Musica(nome, anoLancamento, duracao, artista, genero);

        return dao.criar(musica);

    }

    public Resultado buscarMusicasPlaylist(int playlistId) {
        Resultado resultado = dao.buscarMusicasPorPlaylistId(playlistId);

        if (resultado.foiSucesso()) {
            List<Musica> musicas = (List<Musica>) resultado.comoSucesso().getObj();
            return Resultado.sucesso("Músicas da playlist listadas", musicas);
        } else {
            return Resultado.erro("Erro ao buscar músicas da playlist: " + resultado.getMsg());
        }
    }

    public Resultado listar() {

        Resultado resultado = dao.listar();

        if (resultado.foiSucesso()) {
            // iremos finalizar de montar os objetos
            List<Musica> lista = (List<Musica>) resultado.comoSucesso().getObj();

            for (Musica musica : lista) {
                // buscar o artista da musica, para isso iremos utilizar o ArtistaDAO
                Resultado r1 = artistaDAO.buscarArtistaMusica(musica.getId());
                if (r1.foiErro()) {
                    return r1;
                }
                Artista artista = (Artista) r1.comoSucesso().getObj();
                musica.setArtista(artista);

                // buscar o genero da musica, faremos o mesmo no GeneroDAO
                Resultado r2 = generoDAO.buscarGeneroMusica(musica.getId());
                if (r2.foiErro()) {
                    return r2;
                }
                Genero genero = (Genero) r2.comoSucesso().getObj();
                musica.setGenero(genero);
            }

        }

        return resultado;

    }

}