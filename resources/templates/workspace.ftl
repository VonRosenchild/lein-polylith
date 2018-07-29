<!DOCTYPE html>
<html>
<head>
<title>${workspace}</title>

<link rel="stylesheet" type="text/css" href="style.css">

</head>
<body>

<img src="../logo.png" alt="Polylith" style="width:200px;">

<h1>${workspace}</h1>

<h4>libraries:</h4>
...
<p class="clear"/>

<h4>Interfaces:</h4>
<#list interfaces as interface>
<div class="interface">${interface}</div>
</#list>
<p class="clear"/>

<h4>Components:</h4>
<#list components as component>
  <#if component.name = component.interface>
  <div class="component">${component.name}</div>
  <#else>
  <div class="com-container">
    <div class="com">${component.name}</div>
    <div class="ifc">${component.interface}</div>
  </div>
  </#if>
</#list>
<p class="clear"/>

<h4>Bases:</h4>
<#list bases as base>
<div class="base">${base}</div>
</#list>
<p class="clear"/>

<#list environments as environment>
<h4>${environment.name}:</h4>
  <#list environment.entities as entity>
    <#if entity.type = "base">
<div class="bas">${entity.name}</div>
    <#elseif entity.name = entity.interface>
<div class="component">${entity.name}</div>
    <#else>
<div class="com-container">
  <div class="com">${entity.name}</div>
  <div class="ifc">${entity.interface}</div>
</div>
    </#if>
  </#list>
<p class="clear"/>
</#list>

<#list systems as system>
<h4>${system.name}:</h4>
 <table class="design">
  <#list system.table as row>
  <tr>
    <#list row as col>
      <#if col.type = "spc">
    <td class="spc"></td>
      <#else>
        <#assign class><#if col.type = "base">tbase<#else>comp</#if></#assign>
        <#assign colspan><#if col.columns != 1> colspan=${col.columns}</#if></#assign>
    <td class="${class}"${colspan}>${col.entity}</td>
      </#if>
    </#list>
  </tr>
  </#list>
</table>
</#list>

</body>
</html>