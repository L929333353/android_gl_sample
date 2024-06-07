uniform mat4 uMVPMatrix; //总变换矩阵
attribute vec3 aPosition;  //顶点位置
attribute vec4 aColor;    //顶点颜色
varying  vec4 vColor;  //用于传递给片元着色器的变量

void main()
{
   float alp = sin(pow(aPosition.x, 2.0) + pow(aPosition.y, 2.0) + pow(aPosition.z, 2.0));
   gl_Position = uMVPMatrix * vec4(aPosition,1); //根据总变换矩阵计算此次绘制此顶点位置
   vColor = aColor;//将接收的颜色传递给片元着色器
}