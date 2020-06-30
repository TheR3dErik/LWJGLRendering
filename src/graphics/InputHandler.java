package graphics;

import org.lwjgl.glfw.GLFWKeyCallbackI;
import static org.lwjgl.glfw.GLFW.*;

public class InputHandler implements GLFWKeyCallbackI
{
    private static boolean[] keys = new boolean[65536];

    // This is very clunky and should just be a function pointer instead, but Java doesn't like that :(
    @Override
    public void invoke(long window, int key, int scancode, int action, int mods)
    {
        if (action == GLFW_PRESS)
            keys[key] = true;
        else if (action == GLFW_RELEASE)
            keys[key] = false;
    }

    public static boolean isKeyPressed(int key)
    {
        return keys[key];
    }
}
