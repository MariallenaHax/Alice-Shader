$input a_color0, a_normal, a_position, a_texcoord0
$output v_fog, v_color0, v_clipPosition, v_light, v_texcoord0, v_worldPos

#include <bgfx_shader.sh>
#include <utils/FogUtil.h>
#include <utils/TAAUtil.h>

uniform vec4 FogControl;
uniform vec4 OverlayColor;
uniform vec4 FogColor;
uniform vec4 TileLightColor;
uniform vec4 UVAnimation;
uniform vec4 ZShiftValue;

void main() {
    #if ! defined(RASTERIZED_ALPHA_TEST_PASS)&& ! defined(RASTERIZED_OPAQUE_PASS)&& ! defined(RASTERIZED_TRANSPARENT_PASS)
    vec3 wpos = mul(u_model[0],vec4(a_position, 1.0)).xyz;
    v_worldPos = wpos;
    vec4 position = jitterVertexPosition(wpos);
    #else
    v_worldPos = vec3_splat(1.0);
    #endif
    #if defined(RASTERIZED_ALPHA_TEST_PASS)|| defined(RASTERIZED_OPAQUE_PASS)|| defined(RASTERIZED_TRANSPARENT_PASS)
    vec4 position = mul(u_modelViewProj,vec4(a_position, 1.0));
    #endif
    v_texcoord0 = UVAnimation.xy + (a_texcoord0.xy * UVAnimation.zw);
    position.z += ZShiftValue.x;
    #ifdef LIT__ON
    float L = 1.0;
    vec3 N = normalize((mul(u_model[0],a_normal)).xyz);
    N.y *= TileLightColor.w;
    float yLight = (1.0 + N.y) * 0.5;
    L = yLight * (1.0 - 0.45) + N.x * N.x * -0.1 + N.z * N.z * 0.1 + 0.45;
    vec4 light = vec4(vec3(L, L, L) * TileLightColor.xyz, 1.0);
    light += OverlayColor.a * 0.35;
    v_light = light;
    #else
    v_light = vec4_splat(1.0);
    #endif
    float cameraDepth = position.z;
    float fogIntensity = calculateFogIntensity(cameraDepth, FogControl.z, FogControl.x, FogControl.y);
    v_color0 = a_color0;
    v_fog = vec4(FogColor.rgb, fogIntensity);
    v_clipPosition = position;
    position.y = ndc(position.y);
    gl_Position = position;
}