#include <bgfx_shader.sh>
#include <utils/TonemapUtil.h>

uniform vec4 TintColor;
uniform vec4 HudOpacity;
void main() {
    vec4 diffuse = TintColor;
    diffuse.a = diffuse.a * HudOpacity.x;
    diffuse.rgb=lum_tonemap(diffuse.rgb);
    gl_FragColor = diffuse;
}