#version 430

in vec2 texCoord_FS;
in vec3 position_FS;
in vec3 normal_FS;
in vec4 viewSpacePos;
in vec3 tangent_FS;
in vec3 bitangent_FS;

layout(location = 0) out vec4 albedo_out;
layout(location = 1) out vec4 worldPosition_out;
layout(location = 2) out vec4 normal_out;
layout(location = 3) out vec4 specularEmission_out;
layout(location = 4) out vec4 lightScattering_out;

struct Material
{
	sampler2D diffusemap;
	sampler2D normalmap;
	float shininess;
	float emission;
};

layout (std140, row_major) uniform Camera{
	vec3 eyePosition;
	mat4 m_View;
	mat4 viewProjectionMatrix;
	vec4 frustumPlanes[6];
};

uniform Material material;
uniform float sightRangeFactor;

const float zFar = 10000.0;
const float zNear = 0.1;
const vec3 fogColor = vec3(0.62,0.85,0.95);

float alphaDistanceFactor(float dist)
{
	return clamp(0.01f * (-dist+220),0,1);
}

void main()
{
	vec3 diffuseLight = vec3(0,0,0);
	vec3 specularLight = vec3(0,0,0);
	float diffuseFactor = 0;
	float specularFactor = 0;
	
	float dist = length(eyePosition - position_FS);
	
	mat3 TBN = mat3(tangent_FS, normal_FS, bitangent_FS);
	vec3 normal = normalize(2*(texture(material.normalmap, texCoord_FS * vec2(20,4)).rbg)-1);
	normal = normalize(TBN * normal);
	
	vec3 albedo = texture(material.diffusemap, texCoord_FS * vec2(20,4)).rgb;
	
	float fogFactor = -0.0005/sightRangeFactor*(dist-zFar/5*sightRangeFactor);
	
    vec3 rgb = mix(fogColor, albedo, clamp(fogFactor,0,1));
	
	float alpha = texture(material.diffusemap, texCoord_FS).a;
	alpha *= alphaDistanceFactor(dist);
	
	albedo_out = vec4(rgb,1);
	worldPosition_out = vec4(position_FS,1);
	normal_out = vec4(normal,1);
	specularEmission_out = vec4(1,0,0,1);
	lightScattering_out = vec4(0,0,0,1);
}