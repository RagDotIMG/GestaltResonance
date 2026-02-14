#version 150

uniform sampler2D DiffuseSampler;
uniform float Offset;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    // Chromatic Aberration
    float r = texture(DiffuseSampler, texCoord + vec2(Offset, 0.0)).r;
    float g = texture(DiffuseSampler, texCoord).g;
    float b = texture(DiffuseSampler, texCoord - vec2(Offset, 0.0)).b;
    
    vec3 color = vec3(r, g, b);
    
    // Violet color: 0xBF00FF -> (0.75, 0.0, 1.0)
    vec3 violet = vec3(0.75, 0.0, 1.0);
    
    // Weaker violet tint
    // Mixing the original color with a violet-multiplied version
    // Use 0.15 for a slightly weaker effect as requested
    color = mix(color, color * violet + vec3(0.05, 0.0, 0.08), 0.15);
    
    fragColor = vec4(color, 1.0);
}
