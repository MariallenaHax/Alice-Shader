$input v_texcoord0, v_color0, v_fog, v_lightmapUV,v_worldPos,v_ditheringAndMaskTinting,v_clipPosition

#include <bgfx_shader.sh>
#include <utils/TonemapUtil.h>

uniform vec4 DitherParams2[3];
uniform vec4 DitherParams;
uniform vec4 ViewPositionAndTime;


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

bool dither = false;
#if defined(DITHERING__ON) && (defined(ALPHA_TEST_PASS) || defined(TRANSPARENT_PASS)) 
    if (v_ditheringAndMaskTinting.x > 0.5)
    {
          vec2 phase1 = floor(((((v_clipPosition.xyz / vec3_splat(v_clipPosition.w)).xy * 0.5) + vec2_splat(0.5)) * DitherParams.xy) / vec2_splat(DitherParams2[2].z)) * DitherParams2[2].z;
          vec2 phase2 = floor(phase1 * 0.25);
          vec2 phase3 = floor(phase1 * 0.5);
          vec2 phase4 = floor(phase1);
          dither = smoothstep(DitherParams2[2].x, DitherParams2[2].y, dot(-normalize(u_view[2].xyz), v_worldPos.xyz - ViewPositionAndTime.xyz)) <= (((((((fract((phase2.x * 0.5) + ((phase2.y * phase2.y) * 0.75)) * 0.25) + fract((phase3.x * 0.5) + ((phase3.y * phase3.y) * 0.75))) * 0.25) + fract((phase4.x * 0.5) + ((phase4.y * phase4.y) * 0.75))) * 64.0) + 0.5) * 0.015625);
          #ifdef TRANSPARENT_PASS
          if(dither) 
          {
            diffuse.a = 0.0;
          }
          #endif
    }
    else
    {
          dither = false;
    }
#endif
#ifdef ALPHA_TEST_PASS
      if (dither || (diffuse.a < 0.5))
      {
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
    diffuse.rgb = lum_tonemap(diffuse.rgb,false);
    diffuse.rgb = mix(diffuse.rgb, v_fog, v_fog.a);
    
    gl_FragColor = diffuse;
  #else
    gl_FragColor = vec4_splat(0.0);
  #endif
}