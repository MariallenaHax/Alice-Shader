$input v_color, v_clipPosition, v_worldPos

#include <bgfx_shader.sh>
#include <utils/TonemapUtil.h>

uniform vec4 OverlayColor;
uniform highp vec4 CurrentColor;

void main() {
    vec4 aaa = (CurrentColor * v_color) * vec4(mix(vec3_splat(1.0), OverlayColor.xyz, vec3_splat(OverlayColor.w)), vec4_splat(1.0).w);
    gl_FragColor = vec4(lum_tonemap(aaa,true),aaa.w);
}