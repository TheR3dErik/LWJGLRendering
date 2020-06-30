#version 330 core

//uniform vec3 blockColor;
uniform vec3 backgroundColor;
uniform vec3 lightDir;
uniform vec3 lightPos;

in float cameraDist;
in vec3 vColor;
in vec3 vPos;
flat in vec3 vNormal;

out vec4 FragColor;

void main()
{
    //float mult = 2.0 - 2.0 / (1 + exp(-0.01 * cameraDist));
    //vec3 lightDir = normalize(vec3(-1.0, 3.0, -2.0));

    //vec3 diff = lightPos - vPos;
    //float dist = length(diff);
    //diff = (1.0 / dist) * diff;
    //float lightAmount = min(1.0, max(0.2, dot(vNormal, diff) / (1 + 0.003 * dist * dist)));
    float lightAmount = max(0.2, dot(vNormal, lightDir));
    FragColor = vec4(vColor * lightAmount, 1.0);
    //FragColor = vec4(0.5 * (vNormal + vec3(1.0, 1.0, 1.0)), 1.0); // visualize normals
    //FragColor = vec4(mult*vColor + (1-mult)*backgroundColor, 1.0); // fog
}
