$input v_texcoord0

#include <bgfx_shader.sh>
#include <utils/TonemapUtil.h>

SAMPLER2D_AUTOREG(s_SkyTexture);
void main() {
    vec4 diffuse = vec4(0.4,0.28,0.4,1.0) * texture2D(s_SkyTexture, v_texcoord0);
    diffuse.rgb=lum_tonemap_other(diffuse.rgb);
    gl_FragColor = diffuse;
}