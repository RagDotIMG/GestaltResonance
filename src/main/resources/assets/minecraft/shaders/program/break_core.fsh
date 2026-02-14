#version 150

uniform sampler2D DiffuseSampler;
uniform float Offset;
uniform float Time;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    // Underwater wavy distortion
    vec2 distortedCoord = texCoord;
    distortedCoord.x += sin(distortedCoord.y * 10.0 + Time * 2.0) * 0.005;
    distortedCoord.y += cos(distortedCoord.x * 10.0 + Time * 2.0) * 0.005;

    // Chromatic Aberration
    float r = texture(DiffuseSampler, distortedCoord + vec2(Offset, 0.0)).r;
    float g = texture(DiffuseSampler, distortedCoord).g;
    float b = texture(DiffuseSampler, distortedCoord - vec2(Offset, 0.0)).b;
    
    vec3 color = vec3(r, g, b);
    
    // Violet color: (0.75, 0.0, 1.0) -> 20% darker: (0.6, 0.0, 0.8)
    // Shifted towards red for scarlet tint: (0.7, 0.0, 0.5)
    vec3 scarletViolet = vec3(0.7, 0.0, 0.5);
    
    // Increased tint mix factor to 0.42 (0.35 * 1.2)
    color = mix(color, color * scarletViolet + vec3(0.08, 0.0, 0.04), 0.42);
    
    fragColor = vec4(color, 1.0);
}
