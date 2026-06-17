$input v_texcoord0, v_color0

#include <bgfx_shader.sh>
#include <utils/TonemapUtil.h>

uniform vec4 TintColor;
uniform vec4 HudOpacity;
uniform vec4 ChangeColor;

SAMPLER2D_AUTOREG(s_MatTexture);
void main() {
    vec4 diffuse = texture2D(s_MatTexture, v_texcoord0);

#ifdef MULTI_COLOR_TINT__ON
	vec2 colorMask = diffuse.rg;
	diffuse.rgb = colorMask.rrr * v_color0.rgb;
	diffuse.rgb = mix(diffuse.rgb, colorMask.ggg * ChangeColor.rgb, ceil(colorMask.g));
#else
	diffuse.rgb = mix(diffuse.rgb, diffuse.rgb * v_color0.rgb, diffuse.a);
#endif

	if (v_color0.a > 0.0) {
	diffuse.a = ceil(diffuse.a);
	}

	diffuse *= TintColor;
	diffuse.a = diffuse.a * HudOpacity.x;
    diffuse.rgb=lum_tonemap(diffuse.rgb);
	gl_FragColor = diffuse;
}