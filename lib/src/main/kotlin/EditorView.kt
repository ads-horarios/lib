import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import javax.swing.*

/*fun main() {
    EditorView().open()
}*/

class EditorView {
    private val controller = EditorController()

    val frame = JFrame("JMJSON - JSON Object Editor").apply {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        layout = BorderLayout()
        size = Dimension(900, 900)

        val left = JPanel()
        left.layout = BoxLayout(left, BoxLayout.Y_AXIS)
        left.preferredSize = Dimension(200, 900)
        left.background = Color.LIGHT_GRAY

        val middle = JPanel()
        middle.layout = BoxLayout(middle, BoxLayout.Y_AXIS)
        middle.preferredSize = Dimension(300, 900)
        middle.background = Color.WHITE

        val newKeyButton = JButton("Novo nó")
        newKeyButton.preferredSize = Dimension(260, 40)
        left.add(newKeyButton)

        val deleteKeyButton = JButton("Remover nó (pela key)")
        deleteKeyButton.preferredSize = Dimension(260, 40)
        left.add(deleteKeyButton)

        add(left, BorderLayout.WEST)
        add(middle, BorderLayout.CENTER)

        val right = JPanel(BorderLayout())
        val srcArea = JTextArea()
        srcArea.tabSize = 2
        srcArea.preferredSize = Dimension(300, 900)
        val scrollPane = JScrollPane(srcArea)
        right.add(scrollPane, BorderLayout.CENTER)
        add(right, BorderLayout.EAST)

        controller.observer.addObserver(object : Observer {
            override fun update() {
                srcArea.text = controller.parseToJson(controller.jsonObject)
            }
        })

        newKeyButton.addActionListener {
            val keyName = JOptionPane.showInputDialog("Informe o nome para a chave")
            var teste = ' '

            val dataTypeSelectionDialog = JOptionPane()
            val dataTypes = arrayOf("Array", "Object", "String", "Boolean", "Number", "Null")
            val selectedDataType = JOptionPane.showInputDialog(
                dataTypeSelectionDialog,
                "Selecione o tipo de dados desejado para esta chave",
                "Tipo de Dados",
                JOptionPane.QUESTION_MESSAGE,
                null,
                dataTypes,
                dataTypes[0]
            ) as String

            var jsonValue: JSONValue? = null
            var inputValue = ""

            when (selectedDataType) {
                "Null" -> jsonValue = JSONNull()
                else -> {
                    inputValue = JOptionPane.showInputDialog(null,
                        "Informe o valor desejado\nExemplos:\n  >123 (Number)\n  >abc(String)\n  >[123,'fulano',true] (Array)\n  >false (Bollean)",
                        "Valor",
                        JOptionPane.PLAIN_MESSAGE)
                    jsonValue = controller.parseInput(inputValue, selectedDataType)
                }
            }

            if (jsonValue != null) {
                controller.jsonObject[keyName] = jsonValue
                controller.observer.modifyObject()

               
                val textInput = JTextField(inputValue, 20).apply {
                    addActionListener {
                        controller.jsonObject[keyName] = controller.parseInput(text, selectedDataType)
                        controller.observer.modifyObject()
                    }
                }
                

                middle.add(JLabel("$keyName:"))
                //val text = JTextField(inputValue)
                //middle.add(text)
                middle.add(textInput)
                middle.revalidate()
                middle.repaint()
            }
        }

        deleteKeyButton.addActionListener {
            val keyName = JOptionPane.showInputDialog("Informe o nome da chave a ser deletada (case-sensitive)")
            if(controller.jsonObject.containsKey(keyName)) {
                controller.jsonObject.remove(keyName)
                controller.observer.modifyObject()
            } else {
                JOptionPane.showMessageDialog(null, "Chave inválida, remoção não realizada.", "Error", JOptionPane.ERROR_MESSAGE)
            }
        }
    }

    fun open() {
        frame.isVisible = true
    }
}
