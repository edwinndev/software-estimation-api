<#import "layout.ftlh" as mail>
<@mail.layout>
<p style="margin:0 0 16px;">Hola ${firstName},</p>
<p style="margin:0 0 16px;">Has sido registrado en InScope, la plataforma de estimación de software de Intecx.</p>
<p style="margin:0 0 8px;color:#666666;">Tu código de activación es:</p>
<p style="margin:0 0 16px;font-size:28px;letter-spacing:6px;font-weight:bold;color:#03DDAB;text-align:center;">${otpCode}</p>
<p style="margin:0;">El código es válido por ${expirationHours} horas. Úsalo para configurar tu contraseña e iniciar sesión.</p>
</@mail.layout>
