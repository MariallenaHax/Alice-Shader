$input a_position, a_texcoord0
#ifdef INSTANCING__ON
$input i_data1,i_data2,i_data3
#endif
$output v_texcoord0

uniform mat4 UV0Transform;
#ifdef INSTANCING__OFF
uniform mat4 u_model[4];
#endif
uniform mat4 u_viewProj;
void main() {
#ifdef INSTANCING__OFF
    vec4 _9b079 = mul(u_model[0],vec4(a_position, 1.0));
#endif
#ifdef INSTANCING__ON
    vec4 _78b44 = i_data1;
    vec4 _e67a8 = i_data2;
    vec4 _1b7f0 = i_data3;
    mat4 _e43a8;
    _e43a8[0] = vec4(_78b44.x, _e67a8.x, _1b7f0.x, 0.0);
    _e43a8[1] = vec4(_78b44.y, _e67a8.y, _1b7f0.y, 0.0);
    _e43a8[2] = vec4(_78b44.z, _e67a8.z, _1b7f0.z, 0.0);
    _e43a8[3] = vec4(_78b44.w, _e67a8.w, _1b7f0.w, 1.0);
    vec4 _9b079 = mul(_e43a8,vec4(a_position, 1.0));
#endif
    v_texcoord0 = mul(UV0Transform,vec4(a_texcoord0, 0.0, 1.0)).xy;
    vec4 pos = mul(u_viewProj,vec4(_9b079.xyz, 1.0));
    #if BGFX_SHADER_LANGUAGE_SPIRV
    pos.y = -pos.y;
    #endif
    gl_Position = pos;
}