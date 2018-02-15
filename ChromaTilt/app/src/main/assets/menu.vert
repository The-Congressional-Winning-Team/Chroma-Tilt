attribute vec4 a_position;   

attribute vec2 a_texCoord;

uniform float yOff;

varying vec2 v_texCoord;

void main()
{
   gl_Position = a_position;
   gl_Position.y += yOff;
   v_texCoord = a_texCoord;
}