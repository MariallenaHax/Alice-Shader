$input v_texcoord0, v_color0, v_fog, v_lightmapUV

#include <bgfx_shader.sh>
#include <utils/TonemapUtil.h>

#ifndef DEPTH_ONLY_OPAQUE_PASS
  SAMPLER2D_AUTOREG(s_LightMapTexture);
  SAMPLER2D_AUTOREG(s_MatTexture);

  #if defined(SEASONS__ON) && (defined(ALPHA_TEST_PASS) || defined(OPAQUE_PASS))
    SAMPLER2D_AUTOREG(s_SeasonsTexture);
  #endif
#endif
uniform vec4 FogColor;
void main() {
  #ifndef DEPTH_ONLY_OPAQUE_PASS
    vec4 diffuse = texture2D(s_MatTexture, v_texcoord0);

    #ifdef ALPHA_TEST_PASS
      if (diffuse.a < 0.5) {
        discard;
      }
    #endif

    #if defined(SEASONS__ON) && (defined(ALPHA_TEST_PASS) || defined(OPAQUE_PASS))
      diffuse.rgb *= mix(vec3_splat(1.0), 2.0 * texture2D(s_SeasonsTexture, v_color0.xy).rgb, v_color0.y);
      diffuse.rgb *= v_color0.aaa;
    #else
      diffuse *= v_color0;
    #endif
    diffuse.rgb *= texture2D(s_LightMapTexture, v_lightmapUV).xyz;

    diffuse.rgb = mix(diffuse.rgb, v_fog.rgb, v_fog.a);
    float shadow = mix(0.75,1.0,smoothstep(0.925,0.975,v_lightmapUV.y));
    diffuse.rgb *= mix(shadow,1.0,v_lightmapUV.x);
    diffuse.rgb = lum_tonemap_other(diffuse.rgb);
    gl_FragColor = diffuse;
  #else
    gl_FragColor = vec4_splat(0.0);
  #endif
}