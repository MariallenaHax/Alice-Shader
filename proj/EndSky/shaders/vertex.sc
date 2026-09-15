$input a_position, a_texcoord0
#ifdef INSTANCING__ON
$input i_data1,i_data2,i_data3
#endif
$output v_texcoord0

#include <bgfx_shader.sh>

uniform mat4 UV0Transform;
void main() {
#ifdef INSTANCING__OFF
    vec4 World = mul(u_model[0],vec4(a_position, 1.0));
#endif
#ifdef INSTANCING__ON
    mat4 model;
    model[0] = vec4( i_data1.x, i_data2.x, i_data3.x, 0.0);
    model[1] = vec4( i_data1.y, i_data2.y, i_data3.y, 0.0);
    model[2] = vec4( i_data1.z, i_data2.z, i_data3.z, 0.0);
    model[3] = vec4( i_data1.w, i_data2.w, i_data3.w, 1.0);
    vec4 World = mul(model,vec4(a_position, 1.0));
#endif
    v_texcoord0 = mul(UV0Transform,vec4(a_texcoord0, 0.0, 1.0)).xy;
    vec4 pos = mul(u_viewProj,vec4(World.xyz, 1.0));
    pos.y = ndc(pos.y);
    gl_Position = pos;
}