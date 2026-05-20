$input v_texcoord0, v_color0, v_fog, v_lightmapUV

#include <bgfx_shader.sh>

#ifndef DEPTH_ONLY_OPAQUE
  SAMPLER2D_AUTOREG(s_LightMapTexture);
  SAMPLER2D_AUTOREG(s_MatTexture);

  #if defined(SEASONS) && (defined(ALPHA_TEST) || defined(OPAQUE))
    SAMPLER2D_AUTOREG(s_SeasonsTexture);
  #endif
#endif
uniform vec4 FogColor;
vec3 film(vec3 x)
{
    float a = 2.61;
    float b = 0.93;
    float c = 2.23;
    float d = 1.09;
    float e = 0.94;
    return clamp((x*(a*x+b))/(x*(c*x+d)+e), 0., 1.);
}

vec3 lum_tonemap(vec3 col)
{
    if (col.r == 0.0 && col.g == 0.0 && col.b == 0.0)
        return vec3(0.0,0.0,0.0);

    float gamma = 1.53;
    col = pow(col, float3(1.0 / gamma, 1.0 / gamma, 1.0 / gamma));


    return film(col);
}
void main() {
  #ifndef DEPTH_ONLY_OPAQUE
    vec4 diffuse = texture2D(s_MatTexture, v_texcoord0);

    #ifdef ALPHA_TEST_PASS
      if (diffuse.a < 0.5) {
        discard;
      }
    #endif

    #if defined(SEASONS) && (defined(ALPHA_TEST) || defined(OPAQUE))
      diffuse.rgb *= mix(vec3_splat(1.0), 2.0 * texture2D(s_SeasonsTexture, v_color0.xy).rgb, v_color0.y);
      diffuse.rgb *= v_color0.aaa;
    #else
      diffuse *= v_color0;
    #endif
    diffuse.rgb *= texture2D(s_LightMapTexture, v_lightmapUV).xyz;

    diffuse.rgb = mix(diffuse.rgb, v_fog.rgb, v_fog.a);
    float shadow = mix(0.75,1.0,smoothstep(0.925,0.975,v_lightmapUV.y));
    diffuse.rgb *= mix(shadow,1.0,v_lightmapUV.x);
    diffuse.rgb = lum_tonemap(diffuse.rgb);
    gl_FragColor = diffuse;
  #else
    gl_FragColor = vec4_splat(0.0);
  #endif
}