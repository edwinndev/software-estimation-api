<#import "layout.ftlh" as mail>
<@mail.layout>
<p style="margin:0 0 16px;">Hola,</p>
<p style="margin:0 0 16px;">Solicitaste recuperar tu contraseña en Intecx InScope.</p>
<p style="margin:0 0 8px;color:#666666;">Tu código de verificación es:</p>
<p style="margin:0 0 16px;font-size:28px;letter-spacing:6px;font-weight:bold;color:#03DDAB;text-align:center;">${otpCode}</p>
<p style="margin:0;">El código expira en ${expirationMinutes} minutos. Si no lo pediste, ignora este correo.</p>
</@mail.layout>
