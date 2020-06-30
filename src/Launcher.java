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
    public final int CHUNK_SIZE = 64;

    private PerspectiveCamera camera;

    public void run() {
        window = new Window(720, 480, "Minecraft-like block game?");

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

        Shader shader = Shader.loadFromFile("/home/s/IdeaProjects/LWJGLRendering-master/LWJGLRendering-master/src/graphics/main.vert",
                "/home/s/IdeaProjects/LWJGLRendering-master/LWJGLRendering-master/src/graphics/main.frag");

        chunk = new int[CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE];
        final int AIR = 0x00000000;
        final int DIRT = 0x00000001;
        final int GRASS = 0x00000002;
        final int STONE = 0x00000003;

        for (int y = 0; y < CHUNK_SIZE; y++)
        {
            for (int x = 0; x < CHUNK_SIZE; x++)
            {
                for (int z = 0; z < CHUNK_SIZE; z++)
                {
                    int block = AIR;
                    double height = CHUNK_SIZE/2 + Math.cos(x * 0.10) * CHUNK_SIZE/8 + Math.sin(z * 0.16) * CHUNK_SIZE/8;
                    if (y < height - 4)
                        block = STONE;
                    else if (y < height - 1)
                        block = DIRT;
                    else if (y < height)
                        block = GRASS;

                    chunk[x + z * CHUNK_SIZE + y * CHUNK_SIZE * CHUNK_SIZE] = block;
                }
            }
        }

        /*
        int numIndices = 0;
        int numVertices = 0;
        for (int y = 0; y < CHUNK_SIZE; y++)
        {
            for (int x = 0; x < CHUNK_SIZE; x++)
            {
                for (int z = 0; z < CHUNK_SIZE; z++)
                {
                    if (chunk[x + z * CHUNK_SIZE + y * CHUNK_SIZE * CHUNK_SIZE] == AIR)
                        continue;

                    numVertices += 8;

                    // adds back face if necessary
                    if ((z == 0) || chunk[x + (z-1)*CHUNK_SIZE + y*CHUNK_SIZE*CHUNK_SIZE] == AIR)
                        numIndices += 6;

                    // adds front face if necessary
                    if ((z == CHUNK_SIZE-1) || chunk[x + (z+1)*CHUNK_SIZE + y*CHUNK_SIZE*CHUNK_SIZE] == AIR)
                        numIndices += 6;

                    // adds left face if necessary
                    if ((x == 0) || chunk[x-1 + z*CHUNK_SIZE + y*CHUNK_SIZE*CHUNK_SIZE] == AIR)
                        numIndices += 6;

                    // adds right face if necessary
                    if ((x == CHUNK_SIZE-1) || chunk[x+1 + z*CHUNK_SIZE + y*CHUNK_SIZE*CHUNK_SIZE] == AIR)
                        numIndices += 6;

                    // adds bottom face if necessary
                    if ((y == 0) || chunk[x + z*CHUNK_SIZE + (y-1)*CHUNK_SIZE*CHUNK_SIZE] == AIR)
                        numIndices += 6;

                    // adds top face if necessary
                    if ((y == CHUNK_SIZE-1) || chunk[x + z*CHUNK_SIZE + (y+1)*CHUNK_SIZE*CHUNK_SIZE] == AIR)
                        numIndices += 6;
                }
            }
        }
         */

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
                    if (block == AIR)
                        continue;

                    Vector3f color = new Vector3f(1.0f, 0.0f, 1.0f);
                    if (block == GRASS)
                        color = new Vector3f(20.0f / 255.0f, 219.0f / 255.0f, 40.0f / 255.0f);
                    if (block == DIRT)
                        color = new Vector3f(107.0f/255.0f, 69.0f/255.0f, 43.0f/255.0f);
                    else if (block == STONE)//stone
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

                    // adds back face if necessary
                    if ((z == 0) || chunk[x + (z-1)*CHUNK_SIZE + y*CHUNK_SIZE*CHUNK_SIZE] == AIR)
                    {
                        indicesImproved.add(numBlocks*8 + 0);
                        indicesImproved.add(numBlocks*8 + 2);
                        indicesImproved.add(numBlocks*8 + 1);

                        indicesImproved.add(numBlocks*8 + 0);
                        indicesImproved.add(numBlocks*8 + 3);
                        indicesImproved.add(numBlocks*8 + 2);
                    }

                    // adds front face if necessary
                    if ((z == CHUNK_SIZE-1) || chunk[x + (z+1)*CHUNK_SIZE + y*CHUNK_SIZE*CHUNK_SIZE] == AIR)
                    {
                        indicesImproved.add(numBlocks*8 + 5);
                        indicesImproved.add(numBlocks*8 + 6);
                        indicesImproved.add(numBlocks*8 + 7);

                        indicesImproved.add(numBlocks*8 + 5);
                        indicesImproved.add(numBlocks*8 + 7);
                        indicesImproved.add(numBlocks*8 + 4);
                    }

                    // adds left face if necessary
                    if ((x == 0) || chunk[x-1 + z*CHUNK_SIZE + y*CHUNK_SIZE*CHUNK_SIZE] == AIR)
                    {
                        indicesImproved.add(numBlocks*8 + 3);
                        indicesImproved.add(numBlocks*8 + 0);
                        indicesImproved.add(numBlocks*8 + 4);

                        indicesImproved.add(numBlocks*8 + 3);
                        indicesImproved.add(numBlocks*8 + 4);
                        indicesImproved.add(numBlocks*8 + 7);
                    }

                    // adds right face if necessary
                    if ((x == CHUNK_SIZE-1) || chunk[x+1 + z*CHUNK_SIZE + y*CHUNK_SIZE*CHUNK_SIZE] == AIR)
                    {
                        indicesImproved.add(numBlocks*8 + 1);
                        indicesImproved.add(numBlocks*8 + 6);
                        indicesImproved.add(numBlocks*8 + 5);

                        indicesImproved.add(numBlocks*8 + 1);
                        indicesImproved.add(numBlocks*8 + 2);
                        indicesImproved.add(numBlocks*8 + 6);
                    }

                    // adds bottom face if necessary
                    if ((y == 0) || chunk[x + z*CHUNK_SIZE + (y-1)*CHUNK_SIZE*CHUNK_SIZE] == AIR)
                    {
                        indicesImproved.add(numBlocks*8 + 4);
                        indicesImproved.add(numBlocks*8 + 0);
                        indicesImproved.add(numBlocks*8 + 1);

                        indicesImproved.add(numBlocks*8 + 4);
                        indicesImproved.add(numBlocks*8 + 1);
                        indicesImproved.add(numBlocks*8 + 5);
                    }

                    // adds top face if necessary
                    if ((y == CHUNK_SIZE-1) || chunk[x + z*CHUNK_SIZE + (y+1)*CHUNK_SIZE*CHUNK_SIZE] == AIR)
                    {
                        indicesImproved.add(numBlocks*8 + 6);
                        indicesImproved.add(numBlocks*8 + 3);
                        indicesImproved.add(numBlocks*8 + 7);

                        indicesImproved.add(numBlocks*8 + 6);
                        indicesImproved.add(numBlocks*8 + 2);
                        indicesImproved.add(numBlocks*8 + 3);
                    }
                    numBlocks++;
                }
            }
        }
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
        shader.setUniformMatrix("model", new Matrix4f()); // no transformations applied to mesh
        long lastTime = System.nanoTime();
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (window.isRunning()) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            long now = System.nanoTime();
            System.out.println(1000000000.0/(now - lastTime) + " fps");
            //System.out.println(camera.position);
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
            //shader.setUniform3f("lightDir", camera.getDirectionVector().mul(-1));
            shader.setUniform3f("lightDir", new Vector3f(-1, 3, -2).normalize());
            //shader.setUniform3f("lightPos", camera.position);

            Model.bind(model);
            model.render();

            window.update();
        }

        shader.dispose();
        window.dispose();
    }

    public static void main(String[] args) {
        new Launcher().run();
    }

}
