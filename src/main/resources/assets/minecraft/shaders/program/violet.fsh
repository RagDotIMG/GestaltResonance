#version 150

uniform sampler2D DiffuseSampler;
uniform float Offset;
uniform float Time;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    float age = Time * 20.0; // Convert to "ticks" roughly

    // Distortion matching Tears for Fears Renderer logic
    // float uDist = MathHelper.sin(age * 0.4f * speedMult) * 0.01f * ampMult;
    // float vDist = MathHelper.cos(age * 0.5f * speedMult) * 0.01f * ampMult;
    float uDist = sin(age * 0.4) * 0.01;
    float vDist = cos(age * 0.5) * 0.01;

    vec2 distortedCoord = texCoord + vec2(uDist, vDist);

    // Chromatic Aberration
    float r = texture(DiffuseSampler, distortedCoord + vec2(Offset, 0.0)).r;
    float g = texture(DiffuseSampler, distortedCoord).g;
    float b = texture(DiffuseSampler, distortedCoord - vec2(Offset, 0.0)).b;
    
    vec3 color = vec3(r, g, b);
    
    // Violet color: 0xBF00FF -> (0.75, 0.0, 1.0)
    vec3 violet = vec3(0.75, 0.0, 1.0);
    
    // Pulse matching Tears for Fears Renderer
    // float alpha = 0.65f + pulse * 0.15f; 
    // float boost = 1.0f + (pulse + 1.0f) * 0.2f;
    float pulse = sin(age * 3.14159 * 0.2); // frequency = 2*PI / 10 -> 0.2 * PI
    float boost = 1.0 + (pulse + 1.0) * 0.2;

    // Weaker violet tint
    color = mix(color, color * violet + vec3(0.05, 0.0, 0.08), 0.15);
    color *= boost;
    
    fragColor = vec4(color, 1.0);
}
