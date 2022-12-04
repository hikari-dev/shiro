package dev.hikari.util

import com.kennycason.kumo.CollisionMode
import com.kennycason.kumo.WordCloud
import com.kennycason.kumo.bg.CircleBackground
import com.kennycason.kumo.font.FontWeight
import com.kennycason.kumo.font.KumoFont
import com.kennycason.kumo.font.scale.LinearFontScalar
import com.kennycason.kumo.image.AngleGenerator
import com.kennycason.kumo.nlp.FrequencyAnalyzer
import com.kennycason.kumo.nlp.tokenizers.ChineseWordTokenizer
import com.kennycason.kumo.palette.ColorPalette
import java.awt.Color
import java.awt.Dimension
import java.io.ByteArrayOutputStream


object WordCloudUtils {

    private val tokenizer = ChineseWordTokenizer()

    private val colorList: MutableList<String> by lazy {
        mutableListOf(
            "0000FF",
            "40D3F1",
            "40C5F1",
            "40AAF1",
            "408DF1",
            "4055F1"
        )
    }

    fun generateWordCloud(text: List<String>): ByteArray {
        val frequencyAnalyzer = FrequencyAnalyzer()
        frequencyAnalyzer.setWordFrequenciesToReturn(300)
        frequencyAnalyzer.setMinWordLength(2)
        frequencyAnalyzer.setWordTokenizer(tokenizer)
        val wordFrequencies = frequencyAnalyzer.load(text)
        val dimension = Dimension(600, 600)
        val wordCloud = WordCloud(dimension, CollisionMode.PIXEL_PERFECT)
        wordCloud.setPadding(2)
        wordCloud.setAngleGenerator(AngleGenerator(0))
        wordCloud.setKumoFont(KumoFont("宋体", FontWeight.PLAIN))
        wordCloud.setBackground(CircleBackground(((600 + 600) / 4)))
        wordCloud.setBackgroundColor(Color(0xFFFFFF))
        wordCloud.setColorPalette(ColorPalette(colorList.map {
            it.toIntOrNull(16)?.let { colorInt -> Color(colorInt) }
        }))
        wordCloud.setFontScalar(LinearFontScalar(10, 40))
        wordCloud.build(wordFrequencies)
        val stream = ByteArrayOutputStream()
        wordCloud.writeToStreamAsPNG(stream)
        return stream.toByteArray()
    }
}