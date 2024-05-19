package br.com.alura.screenmatch.model;

public enum Categoria {

    ACAO("Action", "Ação"),
    ROMANCE("Romance", "Romance"),
    COMEDIA("Comedy", "Comédia"),
    DRAMA("Drama", "Drama"),
    CRIME("Crime", "Crime");

    private String categoriaOmdb;
    private String categoriaPt;

    Categoria(String categoriaOmdb, String categoriaPt) {
        this.categoriaOmdb = categoriaOmdb;
        this.categoriaPt = categoriaPt;
    }

    public static Categoria fromString(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaOmdb.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria mapeada para series, foi encontrada.");
    }

    public static Categoria fromStringPortugues (String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaPt.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria mapeada para series, foi encontrada.");
    }
}
