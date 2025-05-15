package task_tracker.managers;

import task_tracker.tasks_type.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node {
        private Node prev;
        private final Task task;
        private Node next;

        public Node(Task task) {
            this.task = task;
        }
    }

    private Node head = null;
    private Node tail = null;
    private final Map<Integer, Node> history;

    public InMemoryHistoryManager() {
        this.history = new HashMap<>();
    }

    @Override
    public List<Task> getHistory() {
        if (history.isEmpty()) {
            return new ArrayList<>();
        }
        List<Task> history = new ArrayList<>(this.history.size());
        Node current = head;
        while (current != null) {
            history.add(current.task);
            current = current.next;
        }
        return history;
    }

    @Override
    public void clearHistory() {
        history.clear();
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        remove(task.getID());
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node nodeToRemove = history.get(id);
        if (nodeToRemove == null) {
            return;
        }
        removeNode(nodeToRemove);
        history.remove(id);
    }

    private void linkLast(Task task) {
        Node newNode = new Node(task);
        if (head == null) {
            head = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
        }
        tail = newNode;
        history.put(task.getID(), newNode);
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }
}
