package graphics;

import math.Vector3f;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;

public class Chunk
{
    public static final int CHUNK_SIZE = 64;

    public int[] chunk;
    public Model model;

    public Chunk(int offsetX, int offsetZ)
    {
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
                    double height = CHUNK_SIZE/2 + Math.cos((x+offsetX) * 0.10) * CHUNK_SIZE/8 + Math.sin((z+offsetZ) * 0.16) * CHUNK_SIZE/8;
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

                    numVertices += 72;

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

        //Model model = new Model(numVertices, numIndices);

        ArrayList<Float> verticesImproved = new ArrayList<Float>();
        ArrayList<Integer> indicesImproved = new ArrayList<Integer>();
        int numBlocks = 0, vertexOffset = 0, indexOffset = 0;
        model = new Model(numVertices, numIndices);
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

            glBindBuffer(GL_ARRAY_BUFFER, model.vbo);
            glBufferSubData(GL_ARRAY_BUFFER, vertexOffset, vertsArray);

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, model.ebo);
            glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, indexOffset, indexsArray);

            vertexOffset += vertsArray.length * 4;
            indexOffset += indexsArray.length * 4;

            verticesImproved.clear();
            indicesImproved.clear();
        }
    }
}
