package graphics;

import math.Matrix4f;
import math.Vector3f;
import utils.FileUtils;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public class Shader
{
    private int programID;
    private Map<String, Integer> locationCache = new HashMap<String, Integer>();

    private Shader() {}

    public static Shader loadFromFile(String vsPath, String fsPath)
    {
        return loadFromString(FileUtils.loadAsString(vsPath), FileUtils.loadAsString(fsPath));
    }

    public static Shader loadFromString(String vsSource, String fsSource)
    {
        Shader shader = new Shader();

        shader.programID = glCreateProgram();
        int vertID = glCreateShader(GL_VERTEX_SHADER);
        int fragID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(vertID, vsSource);
        glShaderSource(fragID, fsSource);

        glCompileShader(vertID);
        if (glGetShaderi(vertID, GL_COMPILE_STATUS) == GL_FALSE)
        {
            System.err.println("Failed to compile vertex shader.");
            System.err.println(glGetShaderInfoLog(vertID));
            return shader;
        }

        glCompileShader(fragID);
        if (glGetShaderi(fragID, GL_COMPILE_STATUS) == GL_FALSE)
        {
            System.err.println("Failed to compile fragment shader.");
            System.err.println(glGetShaderInfoLog(fragID));
            return shader;
        }

        glAttachShader(shader.programID, vertID);
        glAttachShader(shader.programID, fragID);
        glLinkProgram(shader.programID);

        if (glGetProgrami(shader.programID, GL_LINK_STATUS) == GL_FALSE)
        {
            System.err.println("Failed to link shader.");
            System.err.println(glGetProgramInfoLog(shader.programID));
        }
        glValidateProgram(shader.programID);
        if (glGetProgrami(shader.programID, GL_VALIDATE_STATUS) == GL_FALSE)
        {
            System.err.println("Failed to validate shader.");
            System.err.println(glGetProgramInfoLog(shader.programID));
        }

        glDeleteShader(vertID);
        glDeleteShader(fragID);

        glUseProgram(shader.programID);

        return shader;
    }

    public static void bind(Shader shader)
    {
        glUseProgram(shader.programID);
    }

    public static void unbind()
    {
        glUseProgram(0);
    }

    public void dispose()
    {
        glDeleteProgram(programID);
    }

    public int getUniform(String name)
    {
        if (locationCache.containsKey(name))
            return locationCache.get(name);

        int result = glGetUniformLocation(programID, name);
        locationCache.put(name, result);
        if (result == -1)
            System.err.println("Could not locate uniform variable of name: " + name);
        return result;
    }

    public void setUniform1i(String name, int value)
    {
        glUniform1i(getUniform(name), value);
    }

    public void setUniform1f(String name, float value)
    {
        glUniform1f(getUniform(name), value);
    }

    public void setUniform2f(String name, float x, float y)
    {
        glUniform2f(getUniform(name), x, y);
    }

    public void setUniform3f(String name, float x, float y, float z)
    {
        glUniform3f(getUniform(name), x, y, z);
    }

    public void setUniform3f(String name, Vector3f v)
    {
        glUniform3f(getUniform(name), v.x, v.y, v.z);
    }

    public void setUniformMatrix(String name, Matrix4f mat)
    {
        glUniformMatrix4fv(getUniform(name), true, mat.data);
    }

}
