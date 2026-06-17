vec3 film(vec3 x)
{
    float a = 0.95;
    float b = 1.20;
    float c = 0.35;
    float d = 1.10;
    float e = 1.05;

    return clamp((x * (a * x + b)) / (x * (c * x + d) + e), 0.0, 1.0);
}

vec3 lum_tonemap(vec3 col)
{
    if (col.r == 0.0 && col.g == 0.0 && col.b == 0.0)
        return vec3_splat(0.0);

    col = film(col);

    col = col + vec3_splat(0.05);

    float gamma = 0.90;
    float g = 1.0 / gamma;
    col = pow(col, vec3_splat(g));

    return clamp(col, 0.0, 1.0);
}

vec3 film_other(vec3 x)
{
    float a = 2.61;
    float b = 0.93;
    float c = 2.23;
    float d = 1.09;
    float e = 0.94;
    return clamp((x*(a*x+b))/(x*(c*x+d)+e), 0., 1.);
}

vec3 lum_tonemap_other(vec3 col)
{
    if (col.r == 0.0 && col.g == 0.0 && col.b == 0.0)
        return vec3(0.0,0.0,0.0);

    float gamma = 1.53;
    col = pow(col, float3(1.0 / gamma, 1.0 / gamma, 1.0 / gamma));


    return film(col);
}
