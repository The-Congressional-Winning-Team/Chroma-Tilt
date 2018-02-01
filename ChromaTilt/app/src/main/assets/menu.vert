attribute vec4 a_position;   

attribute vec2 a_texCoord;

uniform float xOff;

varying vec2 v_texCoord;

void main()
{
   gl_Position = a_position;
   gl_Position.x += xOff;
   v_texCoord = a_texCoord;
}