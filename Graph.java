package grafo;

import ferramentas.LeitorArquivo;
import java.util.*;
import java.io.*;

enum Cor {
    BRANCO, CINZA, PRETO
}

class AtributosVertice {
    Cor cor;
    int predecessor;
    int tempoDescoberta;
    int tempoFinalizacao;
}

public class Grafo {
    static final Integer INFINITO = Integer.MAX_VALUE;
    static final Integer NULO = -1;
    private int numeroVertices;
    private int numeroArestas;
    private Vector<Vector<Integer>> listaAdjacencia;
    private int[][] matrizAdjacencia;
    private Vector<AtributosVertice> atributosVertices;
    private boolean usarMatriz;
    private boolean dirigido;
    private boolean ponderado;
    private int tempo;

    public Grafo(int numeroVertices, boolean usarMatriz, boolean dirigido, boolean ponderado) {
        this.numeroVertices = numeroVertices;
        this.numeroArestas = 0;
        this.usarMatriz = usarMatriz;
        this.dirigido = dirigido;
        this.ponderado = ponderado;
        
        if (usarMatriz) {
            inicializarMatrizAdjacencia();
        } else {
            inicializarListaAdjacencia();
        }
        inicializarAtributosVertices();
    }

    public Grafo(LeitorArquivo leitor, boolean usarMatriz, boolean dirigido, boolean ponderado) {
        this.usarMatriz = usarMatriz;
        this.dirigido = dirigido;
        this.ponderado = ponderado;
        
        if (usarMatriz) {
            lerGrafoMatriz(leitor);
        } else {
            lerGrafoLista(leitor);
        }
        inicializarAtributosVertices();
    }

    private void inicializarListaAdjacencia() {
        listaAdjacencia = new Vector<>(numeroVertices);
        for (int i = 0; i < numeroVertices; i++) {
            listaAdjacencia.add(new Vector<>());
        }
    }

    private void inicializarMatrizAdjacencia() {
        matrizAdjacencia = new int[numeroVertices][numeroVertices];
        for (int i = 0; i < numeroVertices; i++) {
            for (int j = 0; j < numeroVertices; j++) {
                matrizAdjacencia[i][j] = 0;
            }
        }
    }

    private void inicializarAtributosVertices() {
        atributosVertices = new Vector<>(numeroVertices);
        for (int i = 0; i < numeroVertices; i++) {
            atributosVertices.add(new AtributosVertice());
        }
    }

    private void lerGrafoLista(LeitorArquivo leitor) {
        while (leitor.temProximaLinha()) {
            String linha = leitor.lerProximaLinha().trim();
            if (linha.startsWith("c")) continue;
            else if (linha.startsWith("p")) {
                String[] partes = linha.split(" ");
                this.numeroVertices = Integer.parseInt(partes[2]);
                this.numeroArestas = 0;
                inicializarListaAdjacencia();
            } else if (linha.startsWith("a")) {
                String[] partes = linha.split(" ");
                int origem = Integer.parseInt(partes[1]) - 1;
                int destino = Integer.parseInt(partes[2]) - 1;
                adicionarAresta(origem, destino);
            }
        }
    }

    private void lerGrafoMatriz(LeitorArquivo leitor) {
        while (leitor.temProximaLinha()) {
            String linha = leitor.lerProximaLinha().trim();
            if (linha.startsWith("c")) continue;
            else if (linha.startsWith("p")) {
                String[] partes = linha.split(" ");
                this.numeroVertices = Integer.parseInt(partes[2]);
                this.numeroArestas = 0;
                inicializarMatrizAdjacencia();
            } else if (linha.startsWith("a")) {
                String[] partes = linha.split(" ");
                int origem = Integer.parseInt(partes[1]) - 1;
                int destino = Integer.parseInt(partes[2]) - 1;
                adicionarArestaMatriz(origem, destino);
            }
        }
    }

    public void adicionarAresta(int origem, int destino) {
        listaAdjacencia.get(origem).add(destino);
        if (!dirigido) {
            listaAdjacencia.get(destino).add(origem);
        }
        numeroArestas++;
    }

    public void adicionarArestaMatriz(int origem, int destino) {
        matrizAdjacencia[origem][destino] = 1;
        if (!dirigido) {
            matrizAdjacencia[destino][origem] = 1;
        }
        numeroArestas++;
    }

    public void adicionarArestaPonderada(int origem, int destino, int peso) {
        if (usarMatriz) {
            matrizAdjacencia[origem][destino] = peso;
            if (!dirigido) {
                matrizAdjacencia[destino][origem] = peso;
            }
        } else {
            // Para lista, precisaríamos de uma estrutura diferente para pesos
            listaAdjacencia.get(origem).add(destino);
            if (!dirigido) {
                listaAdjacencia.get(destino).add(origem);
            }
        }
        numeroArestas++;
    }

    public Iterable<Integer> obterVizinhos(int vertice) {
        if (usarMatriz) {
            return obterVizinhosMatriz(vertice);
        } else {
            return listaAdjacencia.get(vertice);
        }
    }

    private List<Integer> obterVizinhosMatriz(int vertice) {
        List<Integer> vizinhos = new ArrayList<>();
        for (int i = 0; i < numeroVertices; i++) {
            if (matrizAdjacencia[vertice][i] != 0) {
                vizinhos.add(i);
            }
        }
        return vizinhos;
    }

    public int obterPeso(int origem, int destino) {
        if (usarMatriz) {
            return matrizAdjacencia[origem][destino];
        } else {
            return 1; // Para lista sem pesos explícitos
        }
    }

    // BUSCA EM LARGURA
    public void buscaEmLargura(int verticeInicial) {
        for (AtributosVertice atributos : atributosVertices) {
            atributos.cor = Cor.BRANCO;
            atributos.distancia = INFINITO;
            atributos.predecessor = NULO;
        }

        atributosVertices.get(verticeInicial).cor = Cor.CINZA;
        atributosVertices.get(verticeInicial).distancia = 0;

        Queue<Integer> fila = new LinkedList<>();
        fila.add(verticeInicial);

        while (!fila.isEmpty()) {
            int verticeAtual = fila.poll();
            for (int vizinho : obterVizinhos(verticeAtual)) {
                if (atributosVertices.get(vizinho).cor == Cor.BRANCO) {
                    atributosVertices.get(vizinho).cor = Cor.CINZA;
                    atributosVertices.get(vizinho).distancia = atributosVertices.get(verticeAtual).distancia + 1;
                    atributosVertices.get(vizinho).predecessor = verticeAtual;
                    fila.add(vizinho);
                }
            }
            atributosVertices.get(verticeAtual).cor = Cor.PRETO;
        }
    }

    // BUSCA EM PROFUNDIDADE
    public void buscaEmProfundidade() {
        tempo = 0;
        for (AtributosVertice atributos : atributosVertices) {
            atributos.cor = Cor.BRANCO;
            atributos.predecessor = NULO;
        }

        for (int i = 0; i < numeroVertices; i++) {
            if (atributosVertices.get(i).cor == Cor.BRANCO) {
                visitarDFS(i);
            }
        }
    }

    private void visitarDFS(int vertice) {
        tempo++;
        atributosVertices.get(vertice).tempoDescoberta = tempo;
        atributosVertices.get(vertice).cor = Cor.CINZA;

        for (int vizinho : obterVizinhos(vertice)) {
            if (atributosVertices.get(vizinho).cor == Cor.BRANCO) {
                atributosVertices.get(vizinho).predecessor = vertice;
                visitarDFS(vizinho);
            }
        }

        atributosVertices.get(vertice).cor = Cor.PRETO;
        tempo++;
        atributosVertices.get(vertice).tempoFinalizacao = tempo;
    }

    // GETTERS
    public int getNumeroVertices() { return numeroVertices; }
    public int getNumeroArestas() { return numeroArestas; }
    public boolean isUsarMatriz() { return usarMatriz; }
    public boolean isDirigido() { return dirigido; }
    public boolean isPonderado() { return ponderado; }

    @Override
    public String toString() {
        StringBuilder construtor = new StringBuilder();
        construtor.append(numeroVertices).append(" vértices, ").append(numeroArestas).append(" arestas\n");
        if (usarMatriz) {
            for (int i = 0; i < numeroVertices; i++) {
                construtor.append(i).append(": ");
                for (int j = 0; j < numeroVertices; j++) {
                    if (matrizAdjacencia[i][j] != 0) {
                        construtor.append(j).append("(").append(matrizAdjacencia[i][j]).append(") ");
                    }
                }
                construtor.append("\n");
            }
        } else {
            for (int i = 0; i < numeroVertices; i++) {
                construtor.append(i).append(": ").append(listaAdjacencia.get(i)).append("\n");
            }
        }
        return construtor.toString();
    }
}