#version 330 core

layout (location=0) in vec3 aPos;
layout (location=1) in vec3 aColor;
layout (location=2) in vec3 aNormal;

uniform mat4 view;
uniform mat4 projection;
uniform mat4 model;

out float cameraDist;
out vec3 vPos;
out vec3 vColor;
flat out vec3 vNormal;

void main()
{
    vec4 viewmodeled = view * model * vec4(aPos, 1.0);
    gl_Position = projection * viewmodeled;
    vPos = aPos;
    vColor = aColor;
    vNormal = aNormal;
    cameraDist = -viewmodeled.z;
    //gl_Position = projection * view * model * vec4(aPos, 1.0);
}