use std::cmp::Ordering;
use std::ops::Deref;

pub struct BinaryTree<T> where T: Ord, {
    value: Option<T>,
    left: Option<Box<BinaryTree<T>>>,
    right: Option<Box<BinaryTree<T>>>,
}

impl<T> Default for BinaryTree<T> where T: Ord, {
    fn default() -> Self {
        Self::new()
    }
}

impl<T> BinaryTree<T> where T: Ord, {
    pub fn new() -> BinaryTree<T> {
        BinaryTree {
            value: None,
            left: None,
            right: None
        }
    }

    pub fn search(&self, value: &T) -> bool {
        match &self.value {
            Some(key) => {
                match key.cmp(value) {
                    Ordering::Equal => true,
                    Ordering::Greater => {
                        match &self.left {
                            Some(node) => node.search(value),
                            None => false
                        }
                    },
                    Ordering::Less => {
                        match &self.right {
                            Some(node) => node.search(value),
                            None => false
                        }
                    }
                }
            }
            None => false,
        }
    }

    pub fn iter(&self) -> impl Iterator<Item = &T> {
        BinaryTreeIter::new(self)
    }

    pub fn insert(&mut self, value: T) {
        if self.value.is_none() {
            self.value = Some(value);
        } else {
            match &self.value {
                None => (),
                Some(key) => {
                    let target_node = if value < *key {
                        &mut self.left
                    } else {
                        &mut self.right
                    };

                    match target_node {
                        Some(ref mut node) => {
                            node.insert(value);
                        }
                        None => {
                            let mut node = BinaryTree::new();
                            node.insert(value);
                            *target_node = Some(Box::new(node));
                        }
                    }
                }
            }
        }
    }

    pub fn min(&self) -> Option<&T> {
        match &self.left {
            Some(node) => node.min(),
            None => self.value.as_ref()
        }
    }

    pub fn max(&self) -> Option<&T> {
        match &self.right {
            Some(node) => node.max(),
            None => self.value.as_ref()
        }
    }

    pub fn floor(&self, value: &T) -> Option<&T> {
        match &self.value {
            Some(key) => {
                if key == value {
                    Some(key)
                } else if key > value {
                    match &self.left {
                        Some(node) => node.floor(value),
                        None => None,
                    }
                } else {
                    match &self.right {
                        Some(node) => {
                            let val = node.floor(value);
                            match val {
                                Some(_) => val,
                                None => Some(key),
                            }
                        },
                        None => Some(key)
                    }
                }
            }
            None => None,
        }
    }

    pub fn ceil(&self, value: &T) -> Option<&T> {
        match &self.value {
            Some(key) => {
                if key == value {
                    Some(key)
                } else if key > value {
                    match &self.left {
                        Some(node) => {
                            let val = node.ceil(value);
                            match val {
                                Some(_) => val,
                                None => Some(key),
                            }
                        },
                        None => Some(key),
                    }
                } else {
                    match &self.left {
                        Some(node) => node.ceil(value),
                        None => None,
                    }
                }
            },
            None => None
        }
    }
}

struct BinaryTreeIter<'a, T> where T: Ord, {
    stack: Vec<&'a BinaryTree<T>>,
}

impl<'a, T> BinaryTreeIter<'a, T> where T: Ord, {
    pub fn new(tree: &BinaryTree<T>) -> BinaryTreeIter<T> {
        let mut iter = BinaryTreeIter{ stack: vec![tree] };
        iter.stack_push_left();
        iter
    }

    fn stack_push_left(&mut self) {
        while let Some(child) = &self.stack.last().unwrap().left {
            self.stack.push(child);
        }
    }
}

impl<'a, T> Iterator for BinaryTreeIter<'a, T> where T:Ord, {
    type Item = &'a T;
    fn next(&mut self) -> Option<&'a T> {
        if self.stack.is_empty() {
            None
        } else {
            let node = self.stack.pop().unwrap();
            if node.right.is_some() {
                self.stack.push(node.right.as_ref().unwrap().deref());
                self.stack_push_left();
            }
            node.value.as_ref()
        }
    }
}

#[cfg(test)]
mod tests {
    use crate::ds::binary_tree::BinaryTree;

    fn tree1() -> BinaryTree<&'static str> {
        let mut tree = BinaryTree::new();
        tree.insert("hello there");
        tree.insert("general knob");
        tree.insert("you are a bold one");
        tree.insert("get him");
        tree.insert("back away");
        tree
    }

    #[test]
    fn test_search() {
        let tree = tree1();
        assert!(tree.search(&"hello there"));
    }

    #[test]
    fn test_minmax() {
        let tree = tree1();
        assert_eq!(*tree.max().unwrap(), "you are a bold one");
        assert_eq!(*tree.min().unwrap(), "back away");
    }

    fn tree2() -> BinaryTree<i32> {
        let mut tree = BinaryTree::new();
        tree.insert(1);
        tree.insert(3);
        tree.insert(5);
        tree.insert(9);
        tree
    }

    #[test]
    fn test_floor_ceil() {
        let tree = tree2();
        let r1 = tree.ceil(&2);
        assert_eq!(&3, r1.unwrap());
        let r2 = tree.floor(&2);
        assert_eq!(&1, r2.unwrap());
    }
}