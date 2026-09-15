vec3 film(vec3 x, bool isOther)
{
    float a = 2.315;
    float b = 0.625;
    float c = 1.525;
    float d = 0.725;
    float e = 0.925;
    if(isOther)
    {
        a = 2.0;
        b = 0.6;
        c = 1.4;
        d = 0.8;
        e = 0.6;
    }
    return saturate((x*(a*x+b))/(x*(c*x+d)+e));
}

vec3 lum_tonemap(vec3 color, bool isOther)
{
    if (color.r+color.g+color.b == 0.0) return vec3_splat(0.0);
    if(isOther)
        return film(pow(color, vec3_splat(1.0)),isOther);
    else
        return film(pow(color, vec3_splat(1.0 / 1.85)),isOther);
}
