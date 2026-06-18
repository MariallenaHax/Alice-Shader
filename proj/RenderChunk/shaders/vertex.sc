$input a_color0, a_position, a_texcoord0, a_texcoord1
#ifdef INSTANCING__ON
    $input i_data1, i_data2, i_data3
#endif
$output v_color0, v_fog, v_texcoord0, v_lightmapUV

#include <bgfx_shader.sh>

uniform vec4 RenderChunkFogAlpha;
uniform vec4 FogAndDistanceControl;
uniform vec4 ViewPositionAndTime;
uniform vec4 FogColor;

void main() {
    uvec2 uv0 = uvec2(round(a_texcoord0 * 65535.0));
    uvec2 _6d79f = uvec2(round(a_texcoord1 * 65535.0));
    uvec2 _5e4ed = _6d79f;
    mat4 model;
#ifdef INSTANCING__ON
    model[0] = vec4(i_data1.x, i_data2.x, i_data3.x, 0.0);
    model[1] = vec4(i_data1.y, i_data2.y, i_data3.y, 0.0);
    model[2] = vec4(i_data1.z, i_data2.z, i_data3.z, 0.0);
    model[3] = vec4(i_data1.w, i_data2.w, i_data3.w, 1.0);
#else
    model = u_model[0];
#endif

    vec3 worldPos = mul(model, vec4(a_position, 1.0)).xyz;
    vec4 color;
#ifdef RENDER_AS_BILLBOARDS__ON
    worldPos += vec3(0.5, 0.5, 0.5);
    vec3 viewDir = normalize(worldPos - ViewPositionAndTime.xyz);
    vec3 boardPlane = normalize(mul(vec3(0.0,1.0,0.0),viewDir));
    worldPos = (worldPos -
        ((((viewDir.yzx * boardPlane.zxy) - (viewDir.zxy * boardPlane.yzx)) *
        (a_color0.z - 0.5)) +
        (boardPlane * (a_color0.x - 0.5))));
    color = vec4(1.0, 1.0, 1.0, 1.0);
#else
    color = a_color0;
#endif

    vec3 camVec = ViewPositionAndTime.xyz - worldPos;
    float camDis = length(camVec);

    vec4 fogColor;
    fogColor.rgb = FogColor.rgb;
    fogColor.a = clamp(
        (((camDis / FogAndDistanceControl.z) + RenderChunkFogAlpha.x) -
         FogAndDistanceControl.x) /
        (FogAndDistanceControl.y - FogAndDistanceControl.x),
        0.0, 1.0
    );
#ifdef TRANSPARENT_PASS
    if(a_color0.a < 0.95) {
        color.a = mix(a_color0.a, 1.0, clamp((camDis / FogAndDistanceControl.w), 0.0, 1.0));
    };
#endif
    vec3 correctedColor = vec3(0.903922,0.903922,0.903922) * color.xyz;
    correctedColor = clamp(correctedColor, 0.0, 1.0);
    vec2 texcoord = vec2(float((uv0.x & 32767u) << uint(1)), float((uv0.y & 32767u) << uint(1))) * vec2_splat(1.525902189314365386962890625e-05);
    texcoord.x += (3.0517578125e-05 * ((2.0 * float((uv0.x & 32768u) >> uint(15))) - 1.0));
    texcoord.y += (3.0517578125e-05 * ((2.0 * float((uv0.y & 32768u) >> uint(15))) - 1.0));
    v_texcoord0 = texcoord;
    v_lightmapUV = vec2(uvec2(_5e4ed.y >> 4u, _5e4ed.y) & uvec2(15u,15u)) * vec2_splat(0.066666670143604278564453125);
    v_color0     = vec4(correctedColor, color.a);
    v_fog        = fogColor;
    vec4 pos = mul(u_viewProj, vec4(worldPos, 1.0));
    #if BGFX_SHADER_LANGUAGE_SPIRV
    pos.y = -pos.y;
    #endif
    gl_Position = pos;
    vec3 l = fract(a_position.xyz*.0625)*16.;
}
