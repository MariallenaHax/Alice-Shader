$input a_position, a_texcoord0, a_color0
$output v_texcoord0, v_color0

#include <bgfx_shader.sh>

void main() {
    v_texcoord0 = a_texcoord0;
    v_color0 = a_color0;
    vec4 pos = mul(u_modelViewProj, vec4(a_position, 1.0));
    pos.y = ndc(pos.y);
    gl_Position = pos;
}