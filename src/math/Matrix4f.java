package math;

public class Matrix4f
{
    public float[] data;

    public Matrix4f()
    {
        this.data = new float[16];
        identity();
    }

    public Matrix4f(float[] otherData)
    {
        this.data = new float[16];

        for (int row = 0; row < 4; row++)
        {
            for (int col = 0; col < 4; col++)
            {
                this.data[row*4 + col] = otherData[row*4 + col];
            }
        }
    }

    public Matrix4f(Matrix4f other)
    {
        this(other.data);
    }

    public Matrix4f identity()
    {
        data[0*4 + 0] = 1;
        data[0*4 + 1] = 0;
        data[0*4 + 2] = 0;
        data[0*4 + 3] = 0;

        data[1*4 + 0] = 0;
        data[1*4 + 1] = 1;
        data[1*4 + 2] = 0;
        data[1*4 + 3] = 0;

        data[2*4 + 0] = 0;
        data[2*4 + 1] = 0;
        data[2*4 + 2] = 1;
        data[2*4 + 3] = 0;

        data[3*4 + 0] = 0;
        data[3*4 + 1] = 0;
        data[3*4 + 2] = 0;
        data[3*4 + 3] = 1;
        return this;
    }

    public Matrix4f translate(float x, float y, float z)
    {
        data[0*4 + 0] = 1;
        data[0*4 + 1] = 0;
        data[0*4 + 2] = 0;
        data[0*4 + 3] = x;

        data[1*4 + 0] = 0;
        data[1*4 + 1] = 1;
        data[1*4 + 2] = 0;
        data[1*4 + 3] = y;

        data[2*4 + 0] = 0;
        data[2*4 + 1] = 0;
        data[2*4 + 2] = 1;
        data[2*4 + 3] = z;

        data[3*4 + 0] = 0;
        data[3*4 + 1] = 0;
        data[3*4 + 2] = 0;
        data[3*4 + 3] = 1;
        return this;
    }

    public Matrix4f translate(Vector3f trans)
    {
        return translate(trans.x, trans.y, trans.z);
    }

    public Matrix4f scale(float x, float y, float z)
    {
        data[0*4 + 0] = x;
        data[0*4 + 1] = 0;
        data[0*4 + 2] = 0;
        data[0*4 + 3] = 0;

        data[1*4 + 0] = 0;
        data[1*4 + 1] = y;
        data[1*4 + 2] = 0;
        data[1*4 + 3] = 0;

        data[2*4 + 0] = 0;
        data[2*4 + 1] = 0;
        data[2*4 + 2] = z;
        data[2*4 + 3] = 0;

        data[3*4 + 0] = 0;
        data[3*4 + 1] = 0;
        data[3*4 + 2] = 0;
        data[3*4 + 3] = 1;
        return this;
    }

    public Matrix4f rotateZ(float angle)
    {
        angle *= 3.141592653589f / 180.0f; //converts to radians

        data[0*4 + 0] = (float)Math.cos(angle);
        data[0*4 + 1] = -(float)Math.sin(angle);
        data[0*4 + 2] = 0;
        data[0*4 + 3] = 0;

        data[1*4 + 0] = (float)Math.sin(angle);
        data[1*4 + 1] = (float)Math.cos(angle);
        data[1*4 + 2] = 0;
        data[1*4 + 3] = 0;

        data[2*4 + 0] = 0;
        data[2*4 + 1] = 0;
        data[2*4 + 2] = 1;
        data[2*4 + 3] = 0;

        data[3*4 + 0] = 0;
        data[3*4 + 1] = 0;
        data[3*4 + 2] = 0;
        data[3*4 + 3] = 1;

        return this;
    }

    public Matrix4f rotateY(float angle)
    {
        angle *= 3.141592653589f / 180.0f; //converts to radians

        data[0*4 + 0] = (float)Math.cos(angle);
        data[0*4 + 1] = 0;
        data[0*4 + 2] = (float)Math.sin(angle);
        data[0*4 + 3] = 0;

        data[1*4 + 0] = 0;
        data[1*4 + 1] = 1;
        data[1*4 + 2] = 0;
        data[1*4 + 3] = 0;

        data[2*4 + 0] = -(float)Math.sin(angle);
        data[2*4 + 1] = 0;
        data[2*4 + 2] = (float)Math.cos(angle);
        data[2*4 + 3] = 0;

        data[3*4 + 0] = 0;
        data[3*4 + 1] = 0;
        data[3*4 + 2] = 0;
        data[3*4 + 3] = 1;

        return this;
    }

    public Matrix4f rotateX(float angle)
    {
        angle *= 3.141592653589f / 180.0f; //converts to radians

        data[0*4 + 0] = 1;
        data[0*4 + 1] = 0;
        data[0*4 + 2] = 0;
        data[0*4 + 3] = 0;

        data[1*4 + 0] = 0;
        data[1*4 + 1] = (float)Math.cos(angle);
        data[1*4 + 2] = -(float)Math.sin(angle);
        data[1*4 + 3] = 0;

        data[2*4 + 0] = 0;
        data[2*4 + 1] = (float)Math.sin(angle);
        data[2*4 + 2] = (float)Math.cos(angle);
        data[2*4 + 3] = 0;

        data[3*4 + 0] = 0;
        data[3*4 + 1] = 0;
        data[3*4 + 2] = 0;
        data[3*4 + 3] = 1;

        return this;
    }

    // not confusing at all
    public Matrix4f scale(Vector3f scale)
    {
        return scale(scale.x, scale.y, scale.z);
    }

    public Matrix4f mul(Matrix4f other)
    {
        for (int row = 0; row < 4; row++)
        {
            // inner loop was unrolled to avoid having to allocate temporary buffer

            float col0 = 0;
            for (int col = 0; col < 4; col++)
            {
                col0 += data[row*4 + col] * other.data[col*4 + 0];
            }

            float col1 = 0;
            for (int col = 0; col < 4; col++)
            {
                col1 += data[row*4 + col] * other.data[col*4 + 1];
            }

            float col2 = 0;
            for (int col = 0; col < 4; col++)
            {
                col2 += data[row*4 + col] * other.data[col*4 + 2];
            }

            float col3 = 0;
            for (int col = 0; col < 4; col++)
            {
                col3 += data[row*4 + col] * other.data[col*4 + 3];
            }

            data[row*4 + 0] = col0;
            data[row*4 + 1] = col1;
            data[row*4 + 2] = col2;
            data[row*4 + 3] = col3;
        }
        return this;
    }

    public static Matrix4f mul(Matrix4f a, Matrix4f b)
    {
        Matrix4f mat = new Matrix4f();
        for (int row = 0; row < 4; row++)
        {
            for (int col = 0; col < 4; col++)
            {
                float sum = 0;
                for (int i = 0; i < 4; i++)
                {
                    sum += a.data[row*4 + i] * b.data[i*4 + col];
                }

                mat.data[row*4 + col] = sum;
            }
        }
        return mat;
    }

    public Matrix4f perspective(float aspect, float fov, float near, float far)
    {
        fov = fov * 3.141592653589f / 180.0f;

        data[0*4 + 0] = 1 / (aspect * (float)Math.tan(0.5*fov));
        data[0*4 + 1] = 0;
        data[0*4 + 2] = 0;
        data[0*4 + 3] = 0;

        data[1*4 + 0] = 0;
        data[1*4 + 1] = 1 / ((float)Math.tan(0.5*fov));
        data[1*4 + 2] = 0;
        data[1*4 + 3] = 0;

        data[2*4 + 0] = 0;
        data[2*4 + 1] = 0;
        data[2*4 + 2] = - (far + near) / (far - near);
        data[2*4 + 3] = - 2 * (far * near) / (far - near);

        data[3*4 + 0] = 0;
        data[3*4 + 1] = 0;
        data[3*4 + 2] = -1;
        data[3*4 + 3] = 0;
        return this;
    }

    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();
        for (int row = 0; row < 4; row++)
        {
            for (int col = 0; col < 4; col++)
            {
                str.append(data[row*4 + col]);
                str.append('\t');
            }
            str.append('\n');
        }
        return str.toString();
    }
}
