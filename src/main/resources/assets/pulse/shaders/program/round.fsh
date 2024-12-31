#version 120

uniform vec4 uColor;       // Color of the quad
uniform vec2 uResolution;  // Resolution of the quad (width and height)
uniform float uRadius;     // Radius of the rounded corners

void main() {
    // Convert fragment coordinates to normalized coordinates
    vec2 pos = gl_FragCoord.xy / uResolution;

    // Calculate the minimum distance to the nearest edge of the rectangle
    vec2 cornerDist = min(pos, 1.0 - pos) * uResolution;

    // Determine the alpha value based on the distance to the rounded corner
    float cornerRadius = min(uRadius, min(uResolution.x, uResolution.y) * 0.5);
    float distToCorner = length(max(cornerRadius - cornerDist, vec2(0.0)));

    // If the pixel is inside the rounded area, render it, otherwise discard
    if (distToCorner > cornerRadius) {
        discard;
    }

    // Set the final color with smooth anti-aliasing
    gl_FragColor = vec4(uColor.rgb, uColor.a * smoothstep(0.0, 1.0, cornerRadius - distToCorner));
}