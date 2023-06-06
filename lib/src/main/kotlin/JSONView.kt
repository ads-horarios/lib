//import java.awt.BorderLayout
//import java.awt.Dimension
//import javax.swing.*
//import java.util.Observer
//
//fun main() {
//    EditorView().open()
//}
//
//class EditorView {
//    private val controller = EditorController()
//
//    val frame = JFrame("JMJSON - JSON Object Editor").apply {
//        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
//        layout = BorderLayout()
//        size = Dimension(900, 900) // Increase window size
//
//        val left = JPanel()
//        left.layout = BoxLayout(left, BoxLayout.Y_AXIS)
//        left.preferredSize = Dimension(300, 900) // Increase panel size
//
//        val middle = JPanel() // New section
//        middle.layout = BoxLayout(middle, BoxLayout.Y_AXIS)
//        middle.preferredSize = Dimension(300, 900)
//
//        val newKeyButton = JButton("New key")
//        newKeyButton.preferredSize = Dimension(260, 40)
//        left.add(newKeyButton)
//
//        val deleteKeyButton = JButton("Delete JSON Key")
//        deleteKeyButton.preferredSize = Dimension(260, 40)
//        left.add(deleteKeyButton)
//
//        add(left, BorderLayout.WEST)
//        add(middle, BorderLayout.CENTER) // Add new section
//
//        val right = JPanel(BorderLayout())
//        val srcArea = JTextArea()
//        srcArea.tabSize = 2
//        val scrollPane = JScrollPane(srcArea)
//        right.add(scrollPane, BorderLayout.CENTER)
//        add(right, BorderLayout.EAST)
//
//        controller.observer.addObserver(Observer { _, _ ->
//            srcArea.text = controller.parseToJson(controller.jsonObject)
//        })
//
//        newKeyButton.addActionListener {
//            val keyName = JOptionPane.showInputDialog("Enter key name")
//
//            val dataTypeSelectionDialog = JOptionPane()
//            val dataTypes = arrayOf("Array", "Object", "String", "Boolean", "Number", "Null")
//            val selectedDataType = JOptionPane.showInputDialog(
//                dataTypeSelectionDialog,
//                "Select data type for the key",
//                "Data Type",
//                JOptionPane.QUESTION_MESSAGE,
//                null,
//                dataTypes,
//                dataTypes[0]
//            ) as String
//
//            var jsonValue: JSONValue? = null
//            when (selectedDataType) {
//                "Null" -> jsonValue = JSONNull()
//                else -> {
//                    val inputValue = JOptionPane.showInputDialog("Enter value")
//                    jsonValue = controller.parseInput(inputValue, selectedDataType)
//                }
//            }
//
//            if (jsonValue != null) {
//                controller.jsonObject[keyName] = jsonValue
//                controller.observer.modifyObject()
//
//                // Create a corresponding text input for the key
//                val textInput = JTextField(20).apply {
//                    addActionListener {
//                        controller.jsonObject[keyName] = controller.parseInput(text, selectedDataType)
//                        controller.observer.modifyObject()
//                    }
//                }
//                middle.add(JLabel("$keyName:"))
//                middle.add(textInput)
//                middle.revalidate()
//                middle.repaint()
//            }
//        }
//
//        deleteKeyButton.addActionListener {
//            val keyName = JOptionPane.showInputDialog("Enter key name to delete")
//            if(controller.jsonObject.containsKey(keyName)) {
//                controller.jsonObject.remove(keyName)
//                controller.observer.modifyObject()
//            } else {
//                JOptionPane.showMessageDialog(null, "Invalid key, deletion not performed.", "Error", JOptionPane.ERROR_MESSAGE)
//            }
//        }
//    }
//
//    fun open() {
//        frame.isVisible = true
//    }
//}