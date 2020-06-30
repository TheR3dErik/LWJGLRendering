import graphics.*;
import math.Matrix4f;
import math.Vector3f;
import org.lwjgl.Version;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL32.GL_FIRST_VERTEX_CONVENTION;
import static org.lwjgl.opengl.GL32.glProvokingVertex;

public class Launcher
{
    // The window handle
    private Window window;

    private PerspectiveCamera camera;
    public static final int CHUNK_GRID = 4;

    public void run() {
        window = new Window(720, 480, "Minecraft-like block game?");

        Chunk[][] chunks = new Chunk[CHUNK_GRID][CHUNK_GRID];

        for (int z = 0; z < CHUNK_GRID; z++)
        {
            for (int x = 0; x < CHUNK_GRID; x++)
            {
                chunks[z][x] = new Chunk(x*Chunk.CHUNK_SIZE, z*Chunk.CHUNK_SIZE);
            }
        }

        camera = new PerspectiveCamera(new Vector3f(Chunk.CHUNK_SIZE,Chunk.CHUNK_SIZE/2 + Chunk.CHUNK_SIZE/8,Chunk.CHUNK_SIZE), window.width * 1.0f / window.height, 60.0f, 1.0f, 1000.0f);
        camera.yaw = 45;

        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        glEnable(GL_DEPTH_TEST);
        // Enables wireframe mode
        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        glProvokingVertex(GL_FIRST_VERTEX_CONVENTION); // allows flat shading with normals

        // culls back faces (which are clockwise)
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glFrontFace(GL_CCW);

        Shader shader = Shader.loadFromFile("./assets/main.vert",
                "./assets/main.frag");

        Vector3f backgroundColor = new Vector3f(87.0f/255.0f, 216.0f/255.0f, 1.0f);
        // Set the clear color to sky colored
        glClearColor(backgroundColor.x, backgroundColor.y, backgroundColor.z, 1.0f);

        Matrix4f modelMat = new Matrix4f();

        Shader.bind(shader);
        shader.setUniformMatrix("projection", camera.getProjectionMatrix());
        shader.setUniform3f("backgroundColor", backgroundColor);
        long lastTime = System.nanoTime();
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        double avgTime = 0.0;
        int numFrames = 0, updateCounter = 60;
        while (window.isRunning()) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            long now = System.nanoTime();
            avgTime += (1000000000.0/(now - lastTime));
            numFrames++;
            if (numFrames % updateCounter == 0)
            {
                System.out.println(avgTime/updateCounter + " fps");
                avgTime = 0.0;
            }
            lastTime = now;

            processInput();

            Shader.bind(shader);
            shader.setUniformMatrix("view", camera.getViewMatrix());
            //shader.setUniform3f("lightDir", camera.getDirectionVector().mul(-1));
            shader.setUniform3f("lightDir", new Vector3f(-1, 3, -2).normalize());
            //shader.setUniform3f("lightPos", camera.position);

            for (int z = 0; z < CHUNK_GRID; z++)
            {
                for (int x = 0; x < CHUNK_GRID; x++)
                {
                    modelMat.translate(x * Chunk.CHUNK_SIZE, 0, z * Chunk.CHUNK_SIZE);
                    shader.setUniformMatrix("model", modelMat);
                    Model.bind(chunks[z][x].model);
                    chunks[z][x].model.render();
                }
            }

            window.update();
        }

        shader.dispose();
        window.dispose();
    }

    private void processInput()
    {
        if (InputHandler.isKeyPressed(GLFW_KEY_ESCAPE))
            window.quit();

        float angularSpeed = 2.5f;
        if (InputHandler.isKeyPressed(GLFW_KEY_UP))
            camera.pitch += angularSpeed;
        if (InputHandler.isKeyPressed(GLFW_KEY_DOWN))
            camera.pitch -= angularSpeed;
        if (InputHandler.isKeyPressed(GLFW_KEY_LEFT))
            camera.yaw += angularSpeed;
        if (InputHandler.isKeyPressed(GLFW_KEY_RIGHT))
            camera.yaw -= angularSpeed;

        float linearSpeed = 0.15f;
        if (InputHandler.isKeyPressed(GLFW_KEY_SPACE))
            camera.position.y += linearSpeed;
        if (InputHandler.isKeyPressed(GLFW_KEY_LEFT_SHIFT))
            camera.position.y -= linearSpeed;

        if (InputHandler.isKeyPressed(GLFW_KEY_W))
        {
            Vector3f forward = camera.getDirectionVector();
            forward.mul(linearSpeed);
            camera.position.add(forward);
        }
        if (InputHandler.isKeyPressed(GLFW_KEY_S))
        {
            Vector3f forward = camera.getDirectionVector();
            forward.mul(linearSpeed);
            camera.position.sub(forward);
        }
        if (InputHandler.isKeyPressed(GLFW_KEY_D))
        {
            Vector3f forward = camera.getRightVector();
            forward.mul(linearSpeed);
            camera.position.add(forward);
        }
        if (InputHandler.isKeyPressed(GLFW_KEY_A))
        {
            Vector3f forward = camera.getLeftVector();
            forward.mul(linearSpeed);
            camera.position.add(forward);
        }
    }

    public static void main(String[] args) {
        new Launcher().run();
    }

}
