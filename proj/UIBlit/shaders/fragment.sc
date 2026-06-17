$input v_texcoord0
#include <bgfx_shader.sh>
#include <utils/TonemapUtil.h>

uniform vec4 TintColor;
uniform vec4 HudOpacity;
SAMPLER2D_AUTOREG(s_MatTexture);
void main() {
    vec4 tex = texture2D(s_MatTexture, v_texcoord0);
    vec4 diffuse = tex * TintColor;
    diffuse.a = diffuse.a * HudOpacity.x;
    diffuse.rgb=lum_tonemap(diffuse.rgb);
    gl_FragColor = diffuse;
}