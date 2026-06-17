$input a_position, a_texcoord0
$output v_texcoord0

#include <bgfx_shader.sh>

void main() {
    v_texcoord0 = a_texcoord0;
    vec4 pos = mul(u_modelViewProj, vec4(a_position, 1.0));
    #if BGFX_SHADER_LANGUAGE_SPIRV
    pos.y = -pos.y;
    #endif
    gl_Position = pos;
}