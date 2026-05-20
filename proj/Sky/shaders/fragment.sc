$input v_color0, v_texcoord0, v_normal, v_worldPos, v_prevWorldPos

#include <bgfx_shader.sh>

void main() {
    v_color0.xyz += vec3(1,0.5,0)*max(v_texcoord0.x-0.5,0.0);
    gl_FragColor = vec4(v_color0.xyz, v_color0.w);
}