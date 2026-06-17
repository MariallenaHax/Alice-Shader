$input v_texcoord0

#include <bgfx_shader.sh>
#include <utils/TonemapUtil.h>

SAMPLER2D_AUTOREG(s_MatTexture);
void main() {
    vec4 diffuse = texture2D(s_MatTexture, v_texcoord0);
    diffuse.rgb=lum_tonemap(diffuse.rgb);
    gl_FragColor = diffuse;
}
