$input a_color0, a_normal, a_position
$output v_color, v_clipPosition, v_worldPos

#include <bgfx_shader.sh>

uniform vec4 SubPixelOffset;
uniform vec4 ZShiftValue;

void main() {
    vec4 var_8815e = mul(u_model[0],vec4(a_position, 1.0));
    mat4 var_83c3f = u_proj;
    vec4 var_67767 = var_83c3f[2];
    var_67767.x += SubPixelOffset.x;
    var_67767.y -= SubPixelOffset.y;
    mat4 var_cbf5d = u_proj;
    var_cbf5d[2] = var_67767;
    vec4 var_79258 = mul(var_cbf5d,mul(u_view,vec4(var_8815e.xyz, 1.0)));
    vec4 var_b1929 = var_79258;
    var_b1929.z += ZShiftValue.x;
    v_clipPosition = var_79258;
    v_color = a_color0;
    v_worldPos = var_8815e.xyz;
    var_b1929.y = ndc(var_b1929.y);
    gl_Position = var_b1929;
}