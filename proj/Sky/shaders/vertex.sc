$input a_color0, a_position, a_texcoord0
    #ifdef INSTANCING
        $input i_data0, i_data1, i_data2
    #endif

$output v_color0, v_texcoord0, v_normal, v_worldPos, v_prevWorldPos

#include <bgfx_shader.sh>

uniform vec4 SkyColor;
uniform vec4 FogColor;

void main()
{
    mat4 model;
#ifdef INSTANCING
    model = mtxFromCols(i_data0, i_data1, i_data2, vec4(0,0,0,1));
#else
    model = u_model[0];
#endif

    vec4 worldPos4 = mul(model, vec4(a_position, 1.0));
    vec3 worldPos  = worldPos4.xyz;

    v_color0 = mix(SkyColor, FogColor, vec4_splat(a_color0.x));

    v_texcoord0 = a_texcoord0;
    v_worldPos  = worldPos;
    v_prevWorldPos = worldPos;
    v_normal = vec3(0.0, 1.0, 0.0);

    gl_Position = mul(u_viewProj, vec4(worldPos, 1.0));
}
