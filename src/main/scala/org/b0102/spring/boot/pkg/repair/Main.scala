package org.b0102.spring.boot.pkg.repair

import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

import org.apache.commons.io.IOUtils
import java.util.zip.ZipOutputStream
import java.io.FileOutputStream
import java.util.zip.Deflater

object Main 
{
  
  def showHelp():Unit =
  {
    println("A utility for repacking modified spring boot executable")
    println("Author: bugbug0102")
    println
    println(s"Usage: [input jar] [output jar]")
  }
  
  def main(args:Array[String]):Unit =
  {
    
    if(args.length >= 2)
    {
      val in = new File(args(0))
      val out = new File(args(1))
      
      var fis:FileInputStream = null
      var zis:ZipInputStream  = null
      
      var fos:FileOutputStream = null
      var zos:ZipOutputStream = null
      try
      {
        fis = new FileInputStream(in)
        zis = new ZipInputStream(fis)
        
        fos = new FileOutputStream(out)
        zos = new ZipOutputStream(fos)
        
        var ze:ZipEntry = zis.getNextEntry
        while(ze!=null)
        {
          val nze = new ZipEntry(ze.getName)
          if(ze.getSize > 0)
          {
            nze.setMethod(ZipOutputStream.STORED)
            nze.setCrc(ze.getCrc)
            nze.setSize(ze.getSize)
          }
          println(s"Working:\t ${ze.getName}")
          
          zos.putNextEntry(nze)
          zos.write(IOUtils.toByteArray(zis))
          zos.closeEntry()
          
          ze = zis.getNextEntry
        }
        
      }finally
      {
        IOUtils.closeQuietly(zis)
        IOUtils.closeQuietly(fis)
        
        IOUtils.closeQuietly(zos)
        IOUtils.closeQuietly(fos)
      }
      
      println("END") 
    }else
    {
      showHelp 
    }
  }
  
}