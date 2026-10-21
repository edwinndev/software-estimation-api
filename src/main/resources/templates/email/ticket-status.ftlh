<#import "layout.ftlh" as mail>
<@mail.layout>
<p style="margin:0 0 16px;">Hola ${firstName},</p>
<p style="margin:0 0 16px;">El ticket <strong>${ticketCode}</strong> cambió de estado.</p>
<table role="presentation" cellpadding="0" cellspacing="0" style="width:100%;background:#f7f7f7;border:1px solid #eeeeee;">
  <tr>
    <td style="padding:8px 12px;color:#666666;width:160px;">Título</td>
    <td style="padding:8px 12px;color:#111111;">${ticketTitle}</td>
  </tr>
  <tr>
    <td style="padding:8px 12px;color:#666666;">Estado anterior</td>
    <td style="padding:8px 12px;color:#111111;">${previousStatus}</td>
  </tr>
  <tr>
    <td style="padding:8px 12px;color:#666666;">Estado actual</td>
    <td style="padding:8px 12px;color:#C41E3A;font-weight:bold;">${newStatus}</td>
  </tr>
</table>
<p style="margin:16px 0 0;">Ingresa a IT Connect para ver el detalle.</p>
</@mail.layout>
