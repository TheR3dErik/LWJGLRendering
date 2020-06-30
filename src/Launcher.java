import graphics.*;
import math.Matrix4f;
import math.Vector3f;
import org.lwjgl.*;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL32.*;

public class Launcher
{
    // The window handle
    private Window window;

    public int[] chunk;
    public final int CHUNK_SIZE = 256;

    private PerspectiveCamera camera;

    public void run() {
        window = new Window(720, 480, "Guess who's back");

        camera = new PerspectiveCamera(new Vector3f(0,CHUNK_SIZE/2,0), window.width * 1.0f / window.height, 60.0f, 1.0f, 1000.0f);
        camera.position.z = 10;

        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        glEnable(GL_DEPTH_TEST);
        // Enables wireframe mode
        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        glProvokingVertex(GL_FIRST_VERTEX_CONVENTION); // allows flat shading with normals

        // culls back faces (which are clockwise)
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glFrontFace(GL_CCW);

        float[] vertices = new float[]{
                  0.0f,  0.0f,  0.0f,
                  1.0f,  0.0f,  0.0f,
                  1.0f,  1.0f,  0.0f,
                  0.0f,  1.0f,  0.0f,
                  0.0f,  0.0f,  1.0f,
                  1.0f,  0.0f,  1.0f,
                  1.0f,  1.0f,  1.0f,
                  0.0f,  1.0f,  1.0f,
        };

        float[] normals = new float[]{
                0.0f, 0.0f, -1.0f,
                1.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f,
                0.0f, -1.0f, 0.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f
        };

        int[] indices = new int[]{
                0, 2, 1,
                0, 3, 2,
                1, 6, 5,
                1, 2, 6,
                5, 7, 4,
                5, 6, 7,
                3, 0, 4,
                3, 4, 7,
                6, 3, 7,
                6, 2, 3,
                4, 0, 1,
                4, 1, 5,
        };

        Shader shader = Shader.loadFromFile("/home/s/IdeaProjects/LWJGLRendering-master/LWJGLRendering-master/src/graphics/main.vert",
                "/home/s/IdeaProjects/LWJGLRendering-master/LWJGLRendering-master/src/graphics/main.frag");

        Matrix4f modelMat = new Matrix4f();

        chunk = new int[CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE];
        final int AIR = 0x00000000;
        final int DIRT = 0x00000001;
        final int GRASS = 0x00000002;
        final int STONE = 0x00000003;

        for (int y = 0; y < 100; y++)
        {
            for (int x = 0; x < CHUNK_SIZE; x++)
            {
                for (int z = 0; z < CHUNK_SIZE; z++)
                {
                    int block = AIR;
                    double height = 64 + Math.cos(x * 0.10) * 8 + Math.sin(z * 0.16) * 8;
                    if (y < height - 4)
                        block = STONE;
                    else if (y < height - 1)
                        block = DIRT;
                    else if (y < height)
                        block = GRASS;

                    // set enable flag
                    if (block != AIR)
                        block = 0x80000000 | block;
                    chunk[x + z * CHUNK_SIZE + y * CHUNK_SIZE * CHUNK_SIZE] = block;
                }
            }
        }

        for (int y = 1; y < CHUNK_SIZE-1; y++)
        {
            for (int x = 1; x < CHUNK_SIZE-1; x++)
            {
                for (int z = 1; z < CHUNK_SIZE-1; z++)
                {
                    // disables visibility flag for block if it does not have any adjacent air
                    int index = x + z * CHUNK_SIZE + y * CHUNK_SIZE * CHUNK_SIZE;
                    int indexLeft = x-1 + z * CHUNK_SIZE + y * CHUNK_SIZE * CHUNK_SIZE;
                    int indexRight = x+1 + z * CHUNK_SIZE + y * CHUNK_SIZE * CHUNK_SIZE;
                    int indexTop = x + z * CHUNK_SIZE + (y-1) * CHUNK_SIZE * CHUNK_SIZE;
                    int indexBottom = x + z * CHUNK_SIZE + (y+1) * CHUNK_SIZE * CHUNK_SIZE;
                    int indexFront = x + (z-1) * CHUNK_SIZE + y * CHUNK_SIZE * CHUNK_SIZE;
                    int indexBack = x + (z+1) * CHUNK_SIZE + y * CHUNK_SIZE * CHUNK_SIZE;
                    if (chunk[index] == AIR || !(chunk[indexLeft] == AIR || chunk[indexRight] == AIR ||
                            chunk[indexTop] == AIR || chunk[indexBottom] == AIR ||
                            chunk[indexFront] == AIR || chunk[indexBack] == AIR))
                    {
                        chunk[index] = chunk[index] & 0xFFFF;
                    }
                }
            }
        }

        ArrayList<Float> verticesImproved = new ArrayList<Float>();
        ArrayList<Integer> indicesImproved = new ArrayList<Integer>();
        int numBlocks = 0;
        for (int y = 0; y < CHUNK_SIZE; y++)
        {
            for (int x = 0; x < CHUNK_SIZE; x++)
            {
                for (int z = 0; z < CHUNK_SIZE; z++)
                {
                    int block = chunk[x + z * CHUNK_SIZE + y * CHUNK_SIZE * CHUNK_SIZE];
                    int bitwiseWhatever = block & 0x80000000;
                    if (bitwiseWhatever == 0x80000000)
                    {
                        int unblocked = block & 0xFFFF;
                        Vector3f color = new Vector3f(1.0f, 0.0f, 1.0f);
                        if (unblocked == GRASS)
                            color = new Vector3f(20.0f / 255.0f, 219.0f / 255.0f, 40.0f / 255.0f);
                        if (unblocked == DIRT)
                            color = new Vector3f(107.0f/255.0f, 69.0f/255.0f, 43.0f/255.0f);
                        else if (unblocked == STONE)//stone
                            color = new Vector3f(0.5f, 0.5f, 0.5f);

                        for (int i = 0; i < 8; i++)
                        {
                            verticesImproved.add(vertices[i*3 + 0] + x);
                            verticesImproved.add(vertices[i*3 + 1] + y);
                            verticesImproved.add(vertices[i*3 + 2] + z);

                            verticesImproved.add(color.x);
                            verticesImproved.add(color.y);
                            verticesImproved.add(color.z);

                            verticesImproved.add(normals[i*3 + 0]);
                            verticesImproved.add(normals[i*3 + 1]);
                            verticesImproved.add(normals[i*3 + 2]);
                        }

                        for (int index : indices) indicesImproved.add(index + numBlocks*8);
                        numBlocks++;
                    }
                }
            }
        }
        //Model model = new Model(vertices, indices);
        float[] vertsArray = new float[verticesImproved.size()];
        for (int i = 0; i < vertsArray.length; i++)
        {
            vertsArray[i] = verticesImproved.get(i);
        }

        int[] indexsArray = new int[indicesImproved.size()];
        for (int i = 0; i < indexsArray.length; i++)
        {
            indexsArray[i] = indicesImproved.get(i);
        }
        Model model = new Model(vertsArray, indexsArray);

        Vector3f backgroundColor = new Vector3f(87.0f/255.0f, 216.0f/255.0f, 1.0f);
        // Set the clear color to sky colored
        glClearColor(backgroundColor.x, backgroundColor.y, backgroundColor.z, 1.0f);

        Shader.bind(shader);
        shader.setUniformMatrix("projection", camera.getProjectionMatrix());
        shader.setUniform3f("backgroundColor", backgroundColor);
        long lastTime = System.nanoTime();
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (window.isRunning()) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            long now = System.nanoTime();
            System.out.println(1000000000.0/(now - lastTime) + " fps");
            lastTime = now;

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

            Shader.bind(shader);
            shader.setUniformMatrix("view", camera.getViewMatrix());
            shader.setUniformMatrix("model", new Matrix4f());

            Model.bind(model);
            model.render();
            /*
            for (int y = 0; y < CHUNK_SIZE; y++)
            {
                for (int x = 0; x < CHUNK_SIZE; x++)
                {
                    for (int z = 0; z < CHUNK_SIZE; z++)
                    {
                        int block = chunk[x + z * CHUNK_SIZE + y * CHUNK_SIZE * CHUNK_SIZE];
                        int bitwiseWhatever = block & 0x80000000;
                        if (bitwiseWhatever == 0x80000000)
                        {
                            modelMat.translate(x, y, z);
                            shader.setUniformMatrix("model", modelMat);

                            int unblocked = block & 0xFFFF;
                            if (unblocked == GRASS)
                                shader.setUniform3f("blockColor", 20.0f/255.0f, 219.0f/255.0f, 40.0f/255.0f);
                            if (unblocked == DIRT)
                                shader.setUniform3f("blockColor", 107.0f/255.0f, 69.0f/255.0f, 43.0f/255.0f);
                            if (unblocked == STONE)
                                shader.setUniform3f("blockColor", 0.5f, 0.5f, 0.5f);

                            Model.bind(model);
                            model.render();
                        }
                    }
                }
            }
             */

            window.update();
        }

        shader.dispose();
        window.dispose();
    }

    public static void main(String[] args) {
        new Launcher().run();
    }

}
