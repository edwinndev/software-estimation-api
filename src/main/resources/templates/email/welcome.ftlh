<#import "layout.ftlh" as mail>
<@mail.layout>
<p style="margin:0 0 16px;">Hola ${firstName},</p>
<p style="margin:0 0 16px;">Tu cuenta en Intecx InScope ya está lista.</p>
<table role="presentation" cellpadding="0" cellspacing="0" style="width:100%;background:#f7f7f7;border:1px solid #eeeeee;">
  <tr>
    <td style="padding:8px 12px;color:#666666;width:160px;">Correo</td>
    <td style="padding:8px 12px;color:#111111;">${email}</td>
  </tr>
</table>
<p style="margin:16px 0 0;">Ya puedes iniciar sesión con tus credenciales.</p>
</@mail.layout>
