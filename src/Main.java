import generation.Generator;
import objects.Player;
import objects.repositories.MeshRepository;
import renderer.Window;

public class Main {
    public static void main(String[] args) {
        Window window = new Window();

//        MeshRepository repository = new MeshRepository();

//        int[][][] states = Generator.getStates();
//        int[][][] lmap = Generator.getLightmap(new Light(10, 10, 10, 1), lmap);

//        repository = Generator.generate(states);

//        window.setRepository(repository);

        Player player = new Player(0, 0, 0, 0);

        window.setPlayer(player);

//        window.states = states;
        window.lmapr = new int[40][40][40];
        window.lmapg = new int[40][40][40];
        window.lmapb = new int[40][40][40];

        window.init();
    }
}
