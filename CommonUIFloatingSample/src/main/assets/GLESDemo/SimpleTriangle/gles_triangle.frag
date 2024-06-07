precision mediump float;
varying vec4 vColor; //接收从顶点着色器过来的参数
uniform float lighting;

void main()
{
   gl_FragColor = vec4(vColor.x * lighting, vColor.y * lighting, vColor.z * lighting, vColor.w);//给此片元颜色值
}