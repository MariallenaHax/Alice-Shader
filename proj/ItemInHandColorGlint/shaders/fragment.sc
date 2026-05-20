$input v_texcoord0, v_color0, v_light, v_fog, v_glintUV

#include <bgfx_shader.sh>
#include <utils/DynamicUtil.h>
#include <utils/FogUtil.h>
#include <utils/TAAUtil.h>
#include <utils/GlintUtil.h>

uniform vec4 ChangeColor;
uniform vec4 OverlayColor;
uniform vec4 ColorBased;
uniform vec4 MultiplicativeTintColor;
uniform vec4 TileLightColor;
uniform vec4 GlintColor;

SAMPLER2D_AUTOREG(s_GlintTexture);
vec3 film(vec3 x)
{
    float a = 1.81;
    float b = 1.63;
    float c = 1.43;
    float d = 1.59;
    float e = 1.44;
    return clamp((x*(a*x+b))/(x*(c*x+d)+e), 0., 1.);
}

vec3 lum_tonemap(vec3 col)
{
    if (col.r == 0.0 && col.g == 0.0 && col.b == 0.0)
        return vec3(0.0,0.0,0.0);

    float gamma = 1.13;
    col = pow(col, float3(1.0 / gamma, 1.0 / gamma, 1.0 / gamma));


    return film(col);
}
void main() {
#if DEPTH_ONLY
    gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);
    return;
#else
    vec4 albedo;
    albedo.rgb = mix(vec3(1.0, 1.0, 1.0), v_color0.rgb, ColorBased.x);
    albedo.a = 1.0;

#if MULTI_COLOR_TINT
    albedo = applyMultiColorChange(albedo, ChangeColor.rgb, MultiplicativeTintColor.rgb);
#else
    albedo = applyColorChange(albedo, ChangeColor, v_color0.a);
#endif

    albedo = applyOverlayColor(albedo, OverlayColor);
    albedo = applyLighting(albedo, v_light);
    albedo = applyGlint(albedo, v_glintUV, s_GlintTexture, GlintColor, TileLightColor);

#if ALPHA_TEST
    if (albedo.a < 0.5) {
        discard;
    }
#endif

    albedo.rgb = applyFog(albedo.rgb, v_fog.rgb, v_fog.a);
    albedo.rgb = lum_tonemap(albedo.rgb);
    gl_FragColor = albedo;
#endif // DEPTH_ONLY
}
