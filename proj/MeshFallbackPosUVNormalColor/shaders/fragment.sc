$input v_fog, v_color0, v_clipPosition, v_light, v_texcoord0, v_worldPos

#include <bgfx_shader.sh>
#include <utils/FogUtil.h>
#include <utils/TonemapUtil.h>

uniform vec4 FogColor;
uniform vec4 FogControl;
uniform vec4 CurrentColor;
uniform vec4 HudOpacity;
uniform vec4 DiscardValue;
uniform vec4 OverlayColor;
uniform vec4 TileLightColor;
uniform vec4 TileLightIntensity;
uniform highp vec4 AlphaMaskedTint;
uniform vec4 ChangeColor;
uniform vec4 UVAnimation;
uniform vec4 ZShiftValue;
uniform highp vec4 DitherParams2[3];
uniform highp vec4 DitherParams;
uniform vec4 DitheringEnabledToggle;

SAMPLER2D_AUTOREG(s_MatTexture);

void main() {
    #ifdef USE_TEXTURES__OFF
    vec4 diffuse = vec4(1, 1, 1, 1);
    #endif
    #ifdef USE_TEXTURES__ON
    vec4 diffuse = texture2D(s_MatTexture, v_texcoord0);
    #endif
    #ifndef ALPHA_TEST__OFF
    bool dither;
    float some;
    if (DitheringEnabledToggle.x != 0.0)
    {
        highp vec2 phase1 = floor(((((v_clipPosition.xyz / vec3_splat(v_clipPosition.w)).xy * 0.5) + vec2_splat(0.5)) * DitherParams.xy) / vec2_splat(DitherParams2[1].z)) * DitherParams2[1].z;
        highp vec2 phase2 = floor(phase1 * 0.25);
        highp vec2 phase3 = floor(phase1 * 0.5);
        highp vec2 phase4 = floor(phase1);
        dither = smoothstep(DitherParams2[1].x, DitherParams2[1].y, dot(-normalize(vec4(u_view[0].z, u_view[1].z, u_view[2].z, some).xyz), v_worldPos - mul(u_invView,vec4(0.0, 0.0, 0.0, 1.0)).xyz)) <= (((((((fract((phase2.x * 0.5) + ((phase2.y * phase2.y) * 0.75)) * 0.25) + fract((phase3.x * 0.5) + ((phase3.y * phase3.y) * 0.75))) * 0.25) + fract((phase4.x * 0.5) + ((phase4.y * phase4.y) * 0.75))) * 64.0) + 0.5) * 0.015625);
    }
    else
    {
        dither = false;
    }
    if (!(AlphaMaskedTint.x != 0.0))
    {
    #endif
    #ifdef ALPHA_TEST__ON_DISCARD_VALUE_BASED
        if ((diffuse.a < DiscardValue.x) || dither)
    #endif
    #ifdef ALPHA_TEST__ON_VERTEX_TINT_MASK_BASED
        if ((diffuse.a <= 0.0) || dither)
    #endif
    #ifndef ALPHA_TEST__OFF
        {
            discard;
        }
    }
    #endif
    #ifdef MULTI_COLOR_TINT__ON
    vec2 colorMask = diffuse.rg;
    diffuse.rgb = colorMask.rrr * v_color0.rgb;
    diffuse.rgb = mix(diffuse.rgb, colorMask.ggg * ChangeColor.rgb, ceil(colorMask.g));
    #endif
    #if defined(ALPHA_TEST__ON_DISCARD_VALUE_BASED)&& defined(LIT__OFF)&& defined(MULTI_COLOR_TINT__OFF)
    if (diffuse.a < DiscardValue.x)
    discard;
    #endif
    diffuse.rgb = mix(diffuse.rgb, OverlayColor.rgb, OverlayColor.a);
    #ifdef LIT__ON
    diffuse *= v_light;
    #endif
    #ifdef MULTI_COLOR_TINT__OFF
    diffuse = CurrentColor * v_color0 * diffuse;
    #endif
    diffuse.a *= HudOpacity.x;
    diffuse.rgb = lum_tonemap(diffuse.rgb,true);
    gl_FragColor = vec4(mix(diffuse, v_fog.rgb, v_fog.a),diffuse.a);
}